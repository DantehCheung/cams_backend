package com.fyp.crms_backend.repository

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.report.DeviceBorrowHistoryResponse
import com.fyp.crms_backend.dto.report.DeviceStatusReportResponse
import com.fyp.crms_backend.dto.report.OverdueDevicesResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ReportRepository(jdbcTemplate: JdbcTemplate, snowflake: Snowflake) :
    ApiRepository(jdbcTemplate, snowflake) {
    // Device Borrow History
    fun getBorrowHistory(studentCNA: String): List<DeviceBorrowHistoryResponse.BorrowRecord> {
        val sql = """
            SELECT d.deviceID, d.deviceName, dbr.borrowDate, dbr.leasePeriod,
                   drr.returnDate, cdr.checkDT, cdr.inspector
            FROM DeviceBorrowRecord dbr
            LEFT JOIN DeviceReturnRecord drr ON dbr.borrowRecordID = drr.borrowRecordID
            LEFT JOIN CheckDeviceReturnRecord cdr ON drr.checkRecordID = cdr.checkRecordID
            JOIN Device d ON dbr.deviceID = d.deviceID
            WHERE dbr.borrowUserCNA = ?
            ORDER BY dbr.borrowDate DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, borrowHistoryRowMapper, studentCNA)
    }

    private val borrowHistoryRowMapper =
        RowMapper<DeviceBorrowHistoryResponse.BorrowRecord> { rs, _ ->
            DeviceBorrowHistoryResponse.BorrowRecord(
                deviceID = rs.getInt("deviceID"),
                deviceName = rs.getString("deviceName"),
                borrowDate = rs.getDate("borrowDate").toLocalDate(),
                dueDate = rs.getDate("leasePeriod").toLocalDate(),
                returnDate = rs.getDate("returnDate")?.toLocalDate(),
                returnStatus = when {
                    rs.getDate("returnDate") == null -> "Not Returned"
                    rs.getDate("returnDate")!!.after(rs.getDate("leasePeriod")) -> "Returned Late"
                    else -> "Returned On Time"
                },
                inspectionTime = rs.getString("checkDT"),
                inspector = rs.getString("inspector")
            )
        }

    // Overdue Devices
    fun getOverdueDevices(
        campusID: Int?,
        roomID: Int?,
        cutoffDate: LocalDate
    ): List<OverdueDevicesResponse.OverdueDevice> {
        val sql = """
            SELECT d.deviceID, d.deviceName, db.borrowUserCNA, 
                   CONCAT(u.firstName, ' ', u.lastName) AS borrowerName,
                   u.contentNo, c.campusShortName, r.roomNumber,
                   db.borrowDate, db.leasePeriod, d.price
            FROM Device d
            JOIN DeviceBorrowRecord db ON d.deviceID = db.deviceID
            JOIN User u ON db.borrowUserCNA = u.CNA
            JOIN Room r ON d.roomID = r.roomID
            JOIN Campus c ON r.campusID = c.campusID
            LEFT JOIN DeviceReturnRecord dr ON db.borrowRecordID = dr.borrowRecordID
            WHERE c.state != 'D' AND r.state != 'D' AND d.state != 'D'
            AND (dr.returnDate IS NULL AND db.leasePeriod <= ?)
            ${if (campusID != null) "AND c.campusID = $campusID" else ""}
            ${if (roomID != null) "AND r.roomID = $roomID" else ""}
            ORDER BY db.leasePeriod
        """.trimIndent()

        return jdbcTemplate.query(sql, overdueDeviceRowMapper, cutoffDate)
    }

    private val overdueDeviceRowMapper = RowMapper<OverdueDevicesResponse.OverdueDevice> { rs, _ ->
        OverdueDevicesResponse.OverdueDevice(
            deviceID = rs.getInt("deviceID"),
            deviceName = rs.getString("deviceName"),
            borrowerCNA = rs.getString("borrowUserCNA"),
            borrowerName = rs.getString("borrowerName"),
            phoneNumber = rs.getString("contentNo"),
            campusName = rs.getString("campusShortName"),
            roomNumber = rs.getString("roomNumber"),
            borrowDate = rs.getDate("borrowDate").toLocalDate(),
            expirationDate = rs.getDate("leasePeriod").toLocalDate(),
            devicePrice = rs.getDouble("price")
        )
    }

    // Device Status Report
    fun getDeviceStatusReport(): List<DeviceStatusReportResponse.DeviceStatus> {
        val sql = """
            SELECT d.deviceID, d.deviceName, d.state, 
                   CONCAT(c.campusShortName, ' ', r.roomNumber) AS location,
                   MAX(u.lastName) AS lastBorrower
            FROM Device d
            LEFT JOIN Room r ON d.roomID = r.roomID
            LEFT JOIN Campus c ON r.campusID = c.campusID
            LEFT JOIN DeviceBorrowRecord dbr ON d.deviceID = dbr.deviceID
            LEFT JOIN User u ON dbr.borrowUserCNA = u.CNA
            WHERE d.state != 'D'
            GROUP BY d.deviceID
        """.trimIndent()

        return jdbcTemplate.query(sql, deviceStatusRowMapper)
    }

    private val deviceStatusRowMapper =
        RowMapper<DeviceStatusReportResponse.DeviceStatus> { rs, _ ->
            DeviceStatusReportResponse.DeviceStatus(
                deviceID = rs.getInt("deviceID"),
                deviceName = rs.getString("deviceName"),
                status = when (rs.getString("state")) {
                    "A" -> "Available"
                    "L" -> "On Loan"
                    else -> rs.getString("state")
                },
                location = rs.getString("location"),
                lastBorrower = rs.getString("lastBorrower")
            )
        }
}