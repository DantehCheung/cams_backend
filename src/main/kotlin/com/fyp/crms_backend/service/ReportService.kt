package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.report.*
import com.fyp.crms_backend.repository.ReportRepository
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportRepository: ReportRepository, jwt: JWT, jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {
    fun getDeviceBorrowHistory(request: DeviceBorrowHistoryRequest): DeviceBorrowHistoryResponse {
        val claims = decryptToken(request.token)
        validateAccess(claims)

        return DeviceBorrowHistoryResponse(
            records = reportRepository.getBorrowHistory(request.studentCNA)
        )
    }

    fun getOverdueDevices(request: OverdueDevicesRequest): OverdueDevicesResponse {
        val claims = decryptToken(request.token)
        validateAccess(claims)

        return OverdueDevicesResponse(
            devices = reportRepository.getOverdueDevices(
                request.campusID,
                request.roomID,
                request.cutoffDate
            )
        )
    }

    fun getDeviceStatusReport(request: DeviceStatusReportRequest): DeviceStatusReportResponse {
        val claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))
        validateAccess(claims)

        return DeviceStatusReportResponse(
            devices = reportRepository.getDeviceStatusReport()
        )
    }

    private fun validateAccess(claims: Claims) {
        if ((claims["accessLevel"] as Int) > 100) {
            throw SecurityException("Insufficient access level")
        }
    }
}