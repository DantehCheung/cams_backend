package com.fyp.crms_backend.controller.item

import com.fyp.crms_backend.dto.item.ItemRequest
import com.fyp.crms_backend.dto.item.ItemResponse
import com.fyp.crms_backend.service.ItemService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ItemController(private val itemService: ItemService) {

    @PostMapping("/getitem")
    fun GetCampus(
        @RequestBody request: ItemRequest
    ): ItemResponse {
        return itemService.execute(request)
    }
}