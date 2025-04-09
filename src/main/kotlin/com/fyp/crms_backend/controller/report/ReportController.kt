// ReportController.kt
package com.fyp.crms_backend.controller.report

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.report.DeviceBorrowHistoryRequest
import com.fyp.crms_backend.dto.report.DeviceStatusReportRequest
import com.fyp.crms_backend.dto.report.OverdueDevicesRequest
import com.fyp.crms_backend.service.ReportService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/report")
class ReportController(private val service: ReportService) : ApiController() {

    @PostMapping("/device-borrow-history")
    fun getDeviceBorrowHistory(
        @RequestBody request: DeviceBorrowHistoryRequest
    ): Response {
        return process(request) {
            return@process service.getDeviceBorrowHistory(request)
        }
    }

    @PostMapping("/overdue-devices")
    fun getOverdueDevices(
        @RequestBody request: OverdueDevicesRequest
    ): Response {
        return process(request) {
            return@process service.getOverdueDevices(request)
        }
    }

    @PostMapping("/device-status-report")
    fun getDeviceStatusReport(
        @RequestBody request: DeviceStatusReportRequest
    ): Response {
        return process(request) {
            return@process service.getDeviceStatusReport(request)
        }
    }
}