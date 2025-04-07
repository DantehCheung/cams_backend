package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class DeleteRfidRequest (
    val token: String,
    val RFID: String,
    val deviceID: Int,
    val partID: Int
) : Request
