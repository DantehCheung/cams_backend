package com.fyp.crms_backend.dto.item


import com.fyp.crms_backend.dto.Request
import java.math.BigDecimal
import java.time.LocalDate

data class UpdatedDeviceDoc(
    val docPath: String,
    val state: Char
)

data class EditItemRequest (
    val token: String,
    val deviceID: Int,
    val deviceName: String,
    val price: BigDecimal,
    val orderDate: LocalDate,
    val arriveDate: LocalDate,
    val maintenanceDate: LocalDate,
    val roomID: Int,
    val state: Char,
    val remark: String,
    val docs: List<UpdatedDeviceDoc>,
) : Request