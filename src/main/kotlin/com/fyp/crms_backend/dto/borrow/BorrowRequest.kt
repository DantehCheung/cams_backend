package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request
import java.time.LocalDate

data class BorrowRequest(
    val token: String,
    val itemID: Int,
    val endDate: LocalDate? = null
) : Request