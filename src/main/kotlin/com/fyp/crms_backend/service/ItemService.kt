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

}


