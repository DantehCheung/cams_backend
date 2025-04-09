package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request
import java.time.LocalDate

data class BorrowListRequest(
    val token: String,
    val targetCNA: String? = null,
    val borrowDateAfter: LocalDate = LocalDate.of(2000, 1, 1),
    val returned: Boolean = false
) : Request
