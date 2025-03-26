package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.room.*
import com.fyp.crms_backend.dto.stateResponse
import com.fyp.crms_backend.repository.RoomRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service

@Service
class RoomService(private val roomRepository: RoomRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: GetRoomRequest): GetRoomResponse {
        val data: Claims = decryptToken(request.token)

        val Rooms = roomRepository.fetchData(data.subject, request.campusID.toString())

        val Room = Rooms.map { room ->
            GetRoomResponse.SingleRoomResponse(
                room = room.roomID!!,
                campusId = room.campusID!!,
                roomNumber = room.roomNumber!!,
                roomName = room.roomName!!
            )
        }

        return GetRoomResponse(
            Rooms = Room
        )
    }


    // Add Room

    fun addRoom(request: AddRoomRequest) : stateResponse {

        val data: Claims = decryptToken(request.token)

        val result : Boolean = roomRepository.addRoom(data.subject,request.campusID,request.roomNumber,request.roomName)

        return stateResponse(
           result
        )
    }



    // Edit Room
    fun editRoom(request:EditRoomRequest) : stateResponse{

        val data: Claims = decryptToken(request.token)

        val result: Boolean = roomRepository.editRoom(data.subject,request.campusID,request.roomID,request.roomNumber,request.roomName)

        return stateResponse(
            result
        )
    }
}