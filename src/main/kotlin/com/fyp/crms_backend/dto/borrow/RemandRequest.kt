package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request
import java.time.LocalDate

// return
data class RemandRequest(
    val token: String,val returnList: List<Int>

):Request