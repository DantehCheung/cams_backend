package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.room.*
import com.fyp.crms_backend.repository.RoomRepository
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository, jwt: JWT, jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {

    fun execute(request: GetRoomRequest): GetRoomResponse {
        val data: Claims = decryptToken(request.token)

        val Rooms = roomRepository.fetchData(data.subject, request.campusID.toString())

        val Room = Rooms.map { room ->
            GetRoomResponse.SingleRoomResponse(
                room = room.roomID!!,
                campusId = room.campusID!!,
                roomNumber = room.roomNumber!!,
                roomName = room.roomName
            )
        }

        return GetRoomResponse(
            Rooms = Room
        )
    }


    // Add Room

    fun addRoom(request: AddRoomRequest): StateResponse {

        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))

        val result: Boolean = roomRepository.addRoom(
            data.subject,
            request.campusID,
            request.roomNumber,
            request.roomName
        )

        return StateResponse(
            result
        )
    }


    // Edit Room
    fun editRoom(request: EditRoomRequest): StateResponse {

        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))

        val result: Boolean = roomRepository.editRoom(
            data.subject,
            request.campusID,
            request.roomID,
            request.roomNumber,
            request.roomName
        )

        return StateResponse(
            result
        )
    }


    // Delete Room
    fun deleteRoom(request: DeleteRoomRequest): StateResponse {
        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))

        val result: Boolean = roomRepository.deleteRoom(data.subject, request.roomID)

        return StateResponse(
            result
        )
    }

    fun newRoom(request: NewRoomRequest): StateResponse {
        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))

        val result: Boolean = roomRepository.newRoom(data.subject, request.roomID, request.roomRFID)

        return StateResponse(
            result
        )
    }
}