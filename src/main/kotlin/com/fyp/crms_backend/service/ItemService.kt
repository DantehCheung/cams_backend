package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.item.AddItemRequest
import com.fyp.crms_backend.dto.item.GetItemRequest
import com.fyp.crms_backend.dto.item.GetItemResponse
import com.fyp.crms_backend.dto.stateResponse
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT) : ApiService(jwt) {


    fun get(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject)
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

        val result: Boolean = itemRepository.addItem(data.subject,request.roomID,request.devices);

        return stateResponse(
            result
        )
    }


}


