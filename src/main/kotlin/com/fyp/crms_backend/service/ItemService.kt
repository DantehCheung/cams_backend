package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.dto.stateResponse
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT) : ApiService(jwt) {


    fun get(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject,request.roomID)
            ?: throw IllegalArgumentException("No campus data found for the user")


        val i: List<GetItemResponse.Item> = repo.map { item ->
            GetItemResponse.Item(
                deviceID = item.deviceID,
                deviceName = item.deviceName,
                price = item.price,
                orderDate = item.orderDate,
                arriveDate = item.arriveDate,
                maintenanceDate = item.maintenanceDate,
                roomID = item.roomID,
                state = item.state,
                remark = item.remark
            )
        }

        return GetItemResponse(
            i= i
        )


    }


    // Add Item
    fun addItem(request: AddItemRequest): stateResponse {

        val data: Claims = decryptToken(request.token)

        val result: Boolean = itemRepository.addItem(data.subject,request.devices);

        return stateResponse(
            result
        )
    }


    // Delete Item
    fun deleteItem(request: DeleteItemRequest): stateResponse{

        val data: Claims = decryptToken(request.token)

        val result: Boolean = itemRepository.deleteItem(data.subject,request.deviceID);

        return stateResponse(
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

}


