package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request
import com.fyp.crms_backend.entity.CAMSDB
import java.math.BigDecimal
import java.time.LocalDate

data class AddItemRequest(
    val token: String,
    val device: Device,
    val deviceParts: List<DevicePart>
) : Request {
    data class Device(
        val deviceName: String,
        val price: BigDecimal,
        val orderDate: LocalDate,
        val arriveDate: LocalDate,
        val maintenanceDate: LocalDate,
        val roomID: Int,
        val state: Char,
        val remark: String,
    )

    data class DevicePart(
        val devicePartName: String,
        val deviceRFID: List<DeviceRFID>,
    )

    data class DeviceRFID(
        val RFID: String? = null
    )

    data class DeviceDoc(
        val docPath: String
    )
}