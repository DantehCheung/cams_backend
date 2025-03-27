package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Response
import java.time.LocalDate
import java.time.LocalDateTime

data class BorrowListResponse(val borrowRecord:List<BorrowRecord>):Response{
    data class BorrowRecord(
        val borrowRecordID: Int,
        val borrowDate: LocalDate,
        val leasePeriod: LocalDate,
        val deviceID: Int,
        val deviceName: String,
        val borrowerCNA: String,
        val borrowerFirstName: String,
        val borrowerLastName: String,
        val returnDate: LocalDate?,
        val checkDate: LocalDateTime?,
        val inspectorCNA: String?,
        val inspectorFirstName: String?,
        val inspectorLastName: String?,
        val roomNumber: String,
        val roomName: String,
        val campusShortName: String
    )
}
