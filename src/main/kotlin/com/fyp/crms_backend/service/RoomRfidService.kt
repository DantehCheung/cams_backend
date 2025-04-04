package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.roomRfid.AddRoomRFIDRequest
import com.fyp.crms_backend.dto.roomRfid.DeleteRoomRFIDRequest
import com.fyp.crms_backend.dto.roomRfid.EditRoomRFIDRequest
import com.fyp.crms_backend.repository.RoomRFIDRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class RoomRFIDService(
    private val roomRFIDRepository: RoomRFIDRepository,
    jwt: JWT, jdbcTemplate: JdbcTemplate
) : ApiService(jwt, jdbcTemplate) {

    fun add(request: AddRoomRFIDRequest): StateResponse {
        val data: Claims = decryptToken(request.token)
        val result = roomRFIDRepository.add(data.subject, request.roomID, request.RFID)
        return StateResponse(result)
    }

    fun edit(request: EditRoomRFIDRequest): StateResponse {
        val data: Claims = decryptToken(request.token)
        val result = roomRFIDRepository.edit(
            data.subject,
            request.RFID,
            request.newRFID,
            request.state
        )
        return StateResponse(result)
    }

    fun delete(request: DeleteRoomRFIDRequest): StateResponse {
        val data: Claims = decryptToken(request.token)
        val result = roomRFIDRepository.delete(data.subject, request.RFID)
        return StateResponse(result)
    }
}