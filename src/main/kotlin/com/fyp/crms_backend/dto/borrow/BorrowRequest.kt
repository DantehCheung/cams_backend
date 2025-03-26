package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request

data class BorrowRequest(
    val token: String,
    val itemID:Int,
):Request