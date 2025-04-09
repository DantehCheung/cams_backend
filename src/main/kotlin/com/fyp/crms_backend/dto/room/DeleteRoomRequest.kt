package com.fyp.crms_backend.dto.room

import com.fyp.crms_backend.dto.Request

data class DeleteRoomRequest(
    val token: String,
    val roomID: Int
) : Request