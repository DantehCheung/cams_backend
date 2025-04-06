package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class updateLocationByRFIDRequest(
    val token: String,
    val roomID: Int,
    val itemList: List<String>
) : Request
