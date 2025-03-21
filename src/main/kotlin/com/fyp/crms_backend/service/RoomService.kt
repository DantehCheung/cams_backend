package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.room.RoomRequest
import com.fyp.crms_backend.dto.room.RoomResponse
import com.fyp.crms_backend.dto.room.SingleRoomResponse
import com.fyp.crms_backend.repository.RoomRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service

@Service
class RoomService(private val roomRepository: RoomRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: RoomRequest): RoomResponse {
        val data: Claims = decryptToken(request.token)

        val Rooms = roomRepository.fetchData(data.subject, request.campusID.toString())

        val Room = Rooms.map { room ->
            SingleRoomResponse(
                room = room.roomID!!,
                campusId = room.campusID!!,
                roomNumber = room.roomNumber!!,
                roomName = room.roomName!!
            )
        }

        return RoomResponse(
            Rooms = Room
        )
    }
}