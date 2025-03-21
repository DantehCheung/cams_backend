package com.fyp.crms_backend.controller.room

import com.fyp.crms_backend.dto.room.RoomRequest
import com.fyp.crms_backend.dto.room.RoomResponse
import com.fyp.crms_backend.service.RoomService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RoomController(private val roomService: RoomService) {

    @PostMapping("/rooms")
    fun getRooms(@RequestBody request: RoomRequest): RoomResponse {
        return roomService.execute(request)
    }
}