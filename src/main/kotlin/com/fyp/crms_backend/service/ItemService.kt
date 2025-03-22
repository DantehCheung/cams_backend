package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.item.GetItemRequest
import com.fyp.crms_backend.dto.item.GetItemResponse
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: GetItemRequest): GetItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject)
            ?: throw IllegalArgumentException("No campus data found for the user")



        return GetItemResponse(
            deviceID = repo.deviceID!!,
            deviceName = repo.deviceName!!,
            price = repo.price!!,
            orderDate = repo.orderDate!!,
            arriveDate = repo.arriveDate!!,
            maintenanceDate = repo.maintenanceDate!!,
            roomID = repo.roomID!!,
            state = repo.state!!,
            remark = repo.remark!!


        )
    }


}


