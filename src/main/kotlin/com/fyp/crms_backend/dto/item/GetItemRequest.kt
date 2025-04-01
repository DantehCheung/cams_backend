package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class GetItemRequest(
    val token: String,
    val roomID: Int,
    val stateList: List<String>? = null
) : Request
