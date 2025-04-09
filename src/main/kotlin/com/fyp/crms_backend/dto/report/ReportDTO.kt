package com.fyp.crms_backend.dto.report


import com.fyp.crms_backend.dto.Request
import com.fyp.crms_backend.dto.Response
import java.time.LocalDate

// Device Borrow History
data class DeviceBorrowHistoryRequest(
    val token: String,
    val studentCNA: String
) : Request

data class DeviceBorrowHistoryResponse(
    val records: List<BorrowRecord>
) : Response {
    data class BorrowRecord(
        val deviceID: Int,
        val deviceName: String,
        val borrowDate: LocalDate,
        val dueDate: LocalDate,
        val returnDate: LocalDate?,
        val returnStatus: String,
        val inspectionTime: String?,
        val inspector: String?
    )
}

// Overdue Devices
data class OverdueDevicesRequest(
    val token: String,
    val campusID: Int?,
    val roomID: Int?,
    val cutoffDate: LocalDate
) : Request

data class OverdueDevicesResponse(
    val devices: List<OverdueDevice>
) : Response {
    data class OverdueDevice(
        val deviceID: Int,
        val deviceName: String,
        val borrowerCNA: String,
        val borrowerName: String,
        val phoneNumber: String,
        val campusName: String,
        val roomNumber: String,
        val borrowDate: LocalDate,
        val expirationDate: LocalDate,
        val devicePrice: Double
    )
}

// Device Status Report
data class DeviceStatusReportRequest(
    val token: String
) : Request

data class DeviceStatusReportResponse(
    val devices: List<DeviceStatus>
) : Response {
    data class DeviceStatus(
        val deviceID: Int,
        val deviceName: String,
        val status: String,
        val location: String,
        val lastBorrower: String?
    )
}