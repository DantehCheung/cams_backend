package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request

data class CheckReturnRequest(val token: String, val RFIDList: List<String>) : Request
