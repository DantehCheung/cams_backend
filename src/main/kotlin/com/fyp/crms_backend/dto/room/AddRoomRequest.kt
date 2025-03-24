package com.fyp.crms_backend.dto.room

import com.fyp.crms_backend.dto.Request

data class AddRoomRequest (
    val token: String,
    val campusID: Int,
    val roomNumber: String,
    val roomName: String
) : Request