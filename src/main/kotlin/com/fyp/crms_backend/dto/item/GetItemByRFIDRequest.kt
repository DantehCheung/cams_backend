package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class GetItemByRFIDRequest(val token: String, val RFID: String) : Request
