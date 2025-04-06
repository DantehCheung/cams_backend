package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT, jdbcTemplate: JdbcTemplate) : ApiService(jwt,jdbcTemplate) {


    fun get(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject, request.roomID, request.stateList)

        return repo


    }


    // Add Item
    fun addItem(request: AddItemRequest): DeviceIdResponse {

        val data: Claims = decryptToken(request.token)

        val deviceID = itemRepository.addItem(data.subject,request.device,request.deviceParts);

        return DeviceIdResponse(
            deviceID
        )
    }


    // Delete Item
    fun deleteItem(request: DeleteItemRequest): StateResponse {

        val data: Claims = decryptToken(request.token)

        val result: Boolean = itemRepository.deleteItem(data.subject,request.deviceID);

        return StateResponse(
            result
        )
    }

    // Edit Item

    fun editItem(request: EditItemRequest): StateResponse {
            // The token in EditItemRequest is used as CNA.
        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.editItem(
            data.subject, request.deviceID, request.deviceName,
            request.price, request.orderDate, request.arriveDate, request.maintenanceDate, request.roomID,
            request.state, request.remark, request.docs
        )

        return StateResponse(
            repo
        )
        }

    // Edit Item Part
    fun editItemPart(request: EditItemPartRequest): StateResponse{

        val data: Claims = decryptToken(request.token)

        val repo: Boolean = itemRepository.editItemPart(
            data.subject,request.deviceID,request.partID,request.partName,request.state
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

    //Manual adjust item
    fun processManualInventory(request: ManualInventoryRequest): ManualInventoryResponse {
        val scannedRFIDs = request.manualInventoryLists.distinct()
        val data: Claims = decryptToken(request.token)

        val initialStates = itemRepository.getDeviceStatesByRFIDs(
            request.roomID,
            scannedRFIDs
        )

        val allDevices = itemRepository.getRoomRFIDInfo(request.roomID)

        val updates = allDevices.associate { device ->
            val newState = when {
                device.RFID in scannedRFIDs -> when (device.currentState) {
                    'L', 'M' -> 'A'  // 扫描到的L/M状态转A
                    else -> device.currentState
                }

                else -> when (device.currentState) {
                    in listOf('A', 'S') -> 'M'  // 未扫描的A/S转M
                    else -> device.currentState
                }
            }
            device.deviceID to newState

        }

        val updatedStates = itemRepository.batchUpdateDeviceStates(updates)

        val unscanned = allDevices.filterNot { it.RFID in scannedRFIDs }


        return ManualInventoryResponse(
            (initialStates.map { device ->
                ManualInventoryResponse.InventoryItem(
                    device.deviceName,
                    device.RFID,
                    device.currentState,
                    updatedStates[device.deviceID] ?: 'E'
                )
            } + unscanned.map { device ->
                ManualInventoryResponse.InventoryItem(
                    device.deviceName,
                    device.RFID,
                    device.currentState,
                    if (device.currentState in listOf('A', 'S', 'R')) 'M' else device.currentState
                )
            }).sortedWith(
                compareByDescending<ManualInventoryResponse.InventoryItem> { it.RFID in scannedRFIDs }
                    .thenBy { it.afterState }
            )
        )
    }

    fun getItemByRFID(request: GetItemByRFIDRequest): GetItemByRFIDResponse {
        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.getItemByRFID(data.subject, request.RFID)

        return repo
    }
}


