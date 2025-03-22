package com.fyp.crms_backend.dto.room

import com.fyp.crms_backend.dto.Request

data class GetRoomRequest(
    val token: String,
    val campusID: Int
) : Request
