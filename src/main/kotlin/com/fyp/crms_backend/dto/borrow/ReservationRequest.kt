package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Request
import java.time.LocalDate

data class ReservationRequest(
    val token: String,
    val itemID:Int,
    val borrowRecordID: LocalDate
):Request