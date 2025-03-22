package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.item.ItemRequest
import com.fyp.crms_backend.dto.item.ItemResponse
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class ItemService(private val itemRepository: ItemRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: ItemRequest): ItemResponse {

        val data: Claims = decryptToken(request.token)

        val repo = itemRepository.fetchData(data.subject)
            ?: throw IllegalArgumentException("No campus data found for the user")



        return ItemResponse(
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


