package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class DeleteDocRequest(
    val token: String,
    val deviceID: Int,
    val partID: Int,
    val docPath: String
) : Request