package com.fyp.crms_backend.dto.item


import com.fyp.crms_backend.dto.Request
import java.math.BigDecimal
import java.time.LocalDate

data class UpdatedDeviceDoc(
    val deviceDocID: Int,
    val docPath: String
)

data class UpdatedDeviceRFID(
    val deviceRFIDID: Int,
    val RFID: String
)

data class UpdatedDevicePart(
    val devicePartID: Int,
    val devicePartName: String,
    val deviceRFID: List<UpdatedDeviceRFID>
)

data class EditItemRequest (
    val token: String,
    val deviceName: String,
    val price: BigDecimal,
    val orderDate: LocalDate,
    val arriveDate: LocalDate,
    val maintenanceDate: LocalDate,
    val roomID: Int,
    val state: Char,
    val remark: String,
    val docs: List<UpdatedDeviceDoc>,
    val deviceParts: List<UpdatedDevicePart>
) : Request