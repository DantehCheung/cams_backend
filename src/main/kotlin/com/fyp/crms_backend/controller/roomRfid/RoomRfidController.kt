package com.fyp.crms_backend.controller.roomRfid

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.roomRfid.AddRoomRFIDRequest
import com.fyp.crms_backend.dto.roomRfid.DeleteRoomRFIDRequest
import com.fyp.crms_backend.dto.roomRfid.EditRoomRFIDRequest
import com.fyp.crms_backend.dto.roomRfid.GetRoomByRFIDRequest
import com.fyp.crms_backend.service.RoomRfidService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RoomRfidController(private val roomRFIDService: RoomRfidService) : ApiController() {

    @PostMapping("/addroomrfid")
    fun addRoomRFID(@RequestBody request: AddRoomRFIDRequest): Response {
        return process(request) { roomRFIDService.add(request) }
    }

    @PostMapping("/editroomrfid")
    fun editRoomRFID(@RequestBody request: EditRoomRFIDRequest): Response {
        return process(request) { roomRFIDService.edit(request) }
    }

    @PostMapping("/deleteroomrfid")
    fun deleteRoomRFID(@RequestBody request: DeleteRoomRFIDRequest): Response {
        return process(request) { roomRFIDService.delete(request) }
    }

    @PostMapping("/getroombyrfid")
    fun getRoomByRFID(@RequestBody request: GetRoomByRFIDRequest): Response {
        return process(request) { roomRFIDService.getRoomByRFID(request) }
    }

}