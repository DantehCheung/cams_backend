package com.fyp.crms_backend.dto.room

data class SingleRoomResponse(
    val room: Int,
    val campusId: Int,
    val roomNumber: String,
    val roomName: String,
)