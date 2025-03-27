package com.fyp.crms_backend.controller.room

import com.fyp.crms_backend.dto.room.AddRoomRequest
import com.fyp.crms_backend.dto.room.EditRoomRequest
import com.fyp.crms_backend.dto.room.GetRoomRequest
import com.fyp.crms_backend.service.RoomService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.room.DeleteRoomRequest

@RestController
@RequestMapping("/api")
class RoomController(private val roomService: RoomService) :ApiController(){

    @PostMapping("/getrooms")
    fun getRooms(@RequestBody request: GetRoomRequest): Response {
       return process(request){
           return@process roomService.execute(request)
       }
    }

    @PostMapping("/addroom")
    fun AddRoom(@RequestBody request: AddRoomRequest): Response{
        return process(request){
           return@process roomService.addRoom(request)
        }
    }

    @PostMapping("/editroom")
    fun EditRoom(@RequestBody request: EditRoomRequest): Response {
        return process(request){
            return@process roomService.editRoom(request)
        }
    }

    @PostMapping("/deleteroom")
    fun DeleteRoom(@RequestBody request: DeleteRoomRequest) : Response {
        return process(request){
            return@process roomService.deleteRoom(request)
        }
    }


}