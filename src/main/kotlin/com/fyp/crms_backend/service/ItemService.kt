package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class ItemService(
    private val itemRepository: ItemRepository, jwt: JWT, jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {


    fun get(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject, request.roomID, request.stateList)

        return repo


    }


    // Add Item
    fun addItem(request: AddItemRequest): DeviceIdResponse {

        val data: Claims = decryptToken(request.token)

        val deviceID = itemRepository.addItem(data.subject, request.device, request.deviceParts)

        return DeviceIdResponse(
            deviceID
        )
    }


    // Delete Item
    fun deleteItem(request: DeleteItemRequest): StateResponse {

        val data: Claims = decryptToken(request.token)

        val result: Boolean = itemRepository.deleteItem(data.subject, request.deviceID)

        return StateResponse(
            result
        )
    }

    // Edit Item

    fun editItem(request: EditItemRequest): StateResponse {
        // The token in EditItemRequest is used as CNA.
        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.editItem(
            data.subject,
            request.deviceID,
            request.deviceName,
            request.price,
            request.orderDate,
            request.arriveDate,
            request.maintenanceDate,
            request.roomID,
            request.state,
            request.remark
        )

        return StateResponse(
            repo
        )
    }

    // Add RFID
    fun addRFID(request: AddRfidRequest): StateResponse {
        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.addSingleRFID(
            data.subject, request.RFID, request.deviceID, request.partID
        )

        return StateResponse(
            repo
        )
    }

    // delete RFID
    fun deleteRFID(request: DeleteRfidRequest): StateResponse {
        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.deleteSingleRFID(
            data.subject, request.RFID, request.deviceID, request.partID
        )

        return StateResponse(
            repo
        )
    }

    // delete Doc
    fun deleteDoc(request: DeleteDocRequest): StateResponse {
        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.deleteSingleDoc(
            data.subject, request.deviceID, request.partID, request.docPath
        )

        return StateResponse(
            repo
        )
    }

    // Edit Item Part
    fun editItemPart(request: EditItemPartRequest): StateResponse {

        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.editItemPart(
            data.subject, request.deviceID, request.partID, request.partName, request.state
        )

        return StateResponse(
            repo
        )

    }

    //Update Item Location Service
    fun updateItemLocation(request: updateLocationByRFIDRequest): updateLocationByRFIDResponse {
        val data: Claims = decryptToken(request.token)

        val deviceParts = itemRepository.getDevicePartsByRFIDs(request.itemList)

        if (deviceParts.isEmpty()) {
            return updateLocationByRFIDResponse(emptyList())
        }

        val groupedByDevice = deviceParts.groupBy { it.deviceID }

        val updateLists = mutableListOf<updateLocationByRFIDResponse.updateList>()

        groupedByDevice.forEach { (deviceId, parts) ->

            val expectedParts = itemRepository.getExpectedPartsForDevice(deviceId)

            if (parts.size == expectedParts.size) {
                itemRepository.updateDeviceLocation(deviceId, request.roomID)
                updateLists.add(
                    updateLocationByRFIDResponse.updateList(
                        deviceName = parts.first().deviceName,
                        successData = updateLocationByRFIDResponse.SuccessData("All parts scanned, location updated")
                    )
                )
            } else {
                val missingParts = expectedParts.filter { expected ->
                    parts.none { scanned -> scanned.devicePartID == expected.devicePartID }
                }
                updateLists.add(
                    updateLocationByRFIDResponse.updateList(
                        deviceName = parts.first().deviceName,
                        failData = updateLocationByRFIDResponse.FailData(
                            "Missing parts",
                            "Missing parts: ${missingParts.joinToString { it.devicePartName }}"
                        )
                    )
                )
            }
        }

        return updateLocationByRFIDResponse(updateLists)
    }

    fun processManualInventory(request: ManualInventoryRequest): ManualInventoryResponse {
        val scannedRFIDs = request.manualInventoryLists.distinct()
        val data: Claims = decryptToken(request.token)

        // Get initial states (for scanned RFIDs)
        val initialStates = itemRepository.getDeviceStatesByRFIDs(
            request.roomID,
            scannedRFIDs
        )

        // Retrieve all device records for the room.
        val allDevices = itemRepository.getRoomRFIDInfo(request.roomID)

        // Group devices by deviceID to compute updates for the whole device.
        val updates: Map<Int, Char> = allDevices.groupBy { it.deviceID }
            .mapValues { (_, deviceParts) ->
                // Use the first record’s state as representative for the whole device
                val currentState = deviceParts.first().currentState

                // For every part (grouped by devicePartID), confirm that at least one RFID was scanned.
                val allPartsScanned = deviceParts
                    .groupBy { it.devicePartID }
                    .all { (_, partEntries) ->
                        partEntries.any { it.RFID in scannedRFIDs }
                    }

                // Update rule:
                // • If state is one of 'M', 'E', 'W', 'S' and every part is scanned -> update to 'A'
                // • If state is 'A' but not all parts are scanned -> revert to 'M'
                when {
                    currentState in listOf('M', 'E', 'W', 'S') && allPartsScanned -> 'A'
                    currentState == 'A' && !allPartsScanned -> 'M'
                    else -> currentState
                }
            }

        // Execute the batch update. This returns a mapping from deviceID to the updated state.
        val updatedStates = itemRepository.batchUpdateDeviceStates(updates)

        // Now, group by (deviceID, devicePartID) to get distinct device parts.
        // Then build the inventory record swapping the devicePartName and RFID values.
        val inventoryItems = allDevices
            .groupBy { it.deviceID to it.devicePartID }
            .map { (_, parts) ->
                val record = parts.first() // representative for the device part group
                ManualInventoryResponse.InventoryItem(
                    deviceName = record.deviceName,
                    // Swap the fields: use the original RFID as the devicePartName and vice versa.
                    devicePartName = record.devicePartName,
                    RFID = record.RFID,
                    preState = record.currentState,
                    afterState = updatedStates[record.deviceID] ?: if (record.currentState in listOf(
                            'A',
                            'S',
                            'R'
                        )
                    ) 'M' else record.currentState
                )
            }
            .sortedWith(
                // For instance, put parts whose (now swapped) devicePartName (originally RFID)
                // is in the scanned list first, then sort by afterState.
                compareByDescending<ManualInventoryResponse.InventoryItem> { it.devicePartName in scannedRFIDs }
                    .thenBy { it.afterState }
            )

        return ManualInventoryResponse(inventoryItems)
    }


    fun getItemByRFID(request: GetItemByRFIDRequest): GetItemByRFIDResponse {
        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.getItemByRFID(data.subject, request.RFID)

        return repo
    }
}


