package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class DeleteItemRequest(
    val token: String,
    val deviceID: Int
) : Request