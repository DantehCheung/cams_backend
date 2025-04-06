package com.fyp.crms_backend.controller.item

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.item.*
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


    @PostMapping("/additem")
    fun addItem(
        @RequestBody request: AddItemRequest
    ): Response {
        return process(request){
            return@process itemService.addItem(request)
        }
    }


    @PostMapping("/edititem") // with device doc
    fun editItem(
        @RequestBody request: EditItemRequest
    ): Response {
        return process(request){
            return@process itemService.editItem(request)
        }
    }

    @PostMapping("/updateitempart")
    fun editItemPart(
        @RequestBody request: EditItemPartRequest
    ) : Response {
        return process(request){
            return@process itemService.editItemPart(request)
        }
    }

    @PostMapping("/deleteitem")
    fun deleteItem(
        @RequestBody request: DeleteItemRequest
    ): Response {
        return process(request){
            return@process itemService.deleteItem(request) // cant merge with edit method, different parameters
        }
    }

    // Update Item Location
    @PostMapping("updateItemLocation")
    fun updateItemLocation(@RequestBody request: updateLocationByRFIDRequest): Response {
        return process(request) {
            return@process itemService.updateItemLocation(request)
        }
    }


    //Manual Inventory item
    @PostMapping("/manualinventory")
    fun manualInventory(
        @RequestBody request: ManualInventoryRequest
    ): Response {
        return process(request) {
            return@process itemService.processManualInventory(request)
        }
    }

    @PostMapping("/getitembyrfid")
    fun getItemByRFID(
        @RequestBody request: GetItemByRFIDRequest
    ): Response {
        return process(request) {
            return@process itemService.getItemByRFID(request)
        }
    }



}