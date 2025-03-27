package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT) : ApiService(jwt) {


    fun get(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject,request.roomID)

        return repo


    }


    // Add Item
    fun addItem(request: AddItemRequest): StateResponse {

        val data: Claims = decryptToken(request.token)

        val result: Boolean = itemRepository.addItem(data.subject,request.devices);

        return StateResponse(
            result
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
    @Service
    class EditItemService(private val itemRepository: ItemRepository) {

        fun editItem(request: EditItemRequest, deviceID: Int): Boolean {
            // The token in EditItemRequest is used as CNA.
            return itemRepository.editItem(request.token, deviceID, request)
        }
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
}


