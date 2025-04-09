package com.fyp.crms_backend.dto.room

import com.fyp.crms_backend.dto.Response

data class GetRoomResponse(
    val Rooms: List<SingleRoomResponse>
) : Response {
    data class SingleRoomResponse(
        val room: Int,
        val campusId: Int,
        val roomNumber: String,
        val roomName: String,
    ) : Response
}