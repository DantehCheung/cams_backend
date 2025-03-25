package com.fyp.crms_backend.controller.item

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.dto.stateResponse
import com.fyp.crms_backend.service.ItemService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ItemController(private val itemService: ItemService) : ApiController(){

    @PostMapping("/getitems")
    fun getItems(
        @RequestBody request: GetItemRequest
    ): Response {
        return process(request){
            return@process itemService.get(request)
        }
    }

    /*
    @PostMapping("/additem")
    fun addItem(
        @RequestBody request: AddItemRequest
    ): Response {
        return process(request){
            return@process itemService.addItem(request)
        }
    }
    *
     */
}