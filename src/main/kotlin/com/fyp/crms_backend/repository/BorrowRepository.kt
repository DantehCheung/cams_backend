package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.borrow.BorrowListResponse
import com.fyp.crms_backend.dto.borrow.CheckReturnResponse
import com.fyp.crms_backend.dto.borrow.RemandResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class BorrowRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

    private fun checkBookingAvailable(itemID: Int, startDate: LocalDate): Boolean {
        // check state and startDate +21 days is not reserved
        return true
    }

    private fun checkReturnAvailable(itemID: Int): Boolean {
        return true
    }

        fun reservation(CNA: String, itemID: Int, borrowDate: LocalDate): Boolean {
        return super.APIprocess(CNA, "reservation") {
            if (!checkBookingAvailable(itemID, borrowDate)) {
                return@APIprocess false
            }

            val result: Int = jdbcTemplate.update(
                """INSERT INTO deviceborrowrecord
                        (
                        borrowDate,
                        deviceID,
                        borrowUserCNA,
                        leasePeriod)
                        VALUES(
                        ?,
                        ?,
                        ?,
                        ?)""".trimIndent(),
                borrowDate, itemID, CNA, borrowDate.plusDays(14)
            )

            return@APIprocess result > 0
        } as Boolean

    }

    @Transactional
    fun borrow(CNA: String, itemID: Int): Boolean {
        return super.APIprocess(CNA, "borrow") {
            if (!checkBookingAvailable(itemID, LocalDate.now())) {
                return@APIprocess false
            }
            val result: Int = jdbcTemplate.update(
                """INSERT INTO `deviceborrowrecord` (`deviceID`, `borrowUserCNA`) VALUES ( ?, ?)""",
                itemID, CNA
            )
            jdbcTemplate.update(
                """UPDATE device
                    SET state = 'L'
                    WHERE deviceID = ?""".trimIndent(),
                itemID
            )
            return@APIprocess result > 0
        } as Boolean
    }


    fun remand(CNA: String, returnList: List<Int>): List<RemandResponse.deviceResult> {

        return super.APIprocess(CNA, "remand") {
            val stateList: List<RemandResponse.deviceResult> = returnList.map { itemID ->
                RemandResponse.deviceResult(itemID = itemID, state = false)
            }
            returnList.map { itemID ->
                if (!checkReturnAvailable(itemID)) {
                    return@APIprocess false
                }
                val result: Int = jdbcTemplate.update(
                    """INSERT INTO devicereturnrecord (borrowRecordID)
SELECT borrowRecordID
FROM deviceborrowrecord
WHERE deviceID = ?""".trimIndent(),
                    itemID
                )
                stateList.find { it.itemID == itemID }?.state = result > 0

            }
            return@APIprocess stateList
        } as List<RemandResponse.deviceResult>

    }
//    """INSERT INTO `cams`.`checkdevicereturnrecord`
//(`checkRecordID`,
//`checkDT`,
//`inspector`)
//VALUES
//(<{checkRecordID: }>,
//<{checkDT: }>,
//<{inspector: }>);
//"""
//    """INSERT INTO `cams`.`checkdevicereturnrecord`
//(`checkRecordID`, `checkDT`, `inspector`)
//VALUES
//(1, '2025-03-26 12:00:00', 'John Doe'),
//(2, '2025-03-26 12:30:00', 'Jane Smith'),
//(3, '2025-03-26 13:00:00', 'Mike Johnson');"""
    fun getBorrowList(
        CNA: String,
        targetCNA:String? = null,
        borrowDateAfter: LocalDate = LocalDate.of(2000, 1, 1),
        returned: Boolean = false
    ): List<BorrowListResponse.BorrowRecord> {
        return super.APIprocess(CNA, "getBorrowList") {

            val query = """
    SELECT
        br.borrowRecordID,
        br.borrowDate,
        br.leasePeriod,
        d.deviceID,
        d.deviceName,
        u.CNA AS borrowerCNA,
        u.firstName AS borrowerFirstName,
        u.lastName AS borrowerLastName,
        rr.returnDate,
        c.checkDT AS checkDate,
        inspector.CNA AS inspectorCNA,
        inspector.firstName AS inspectorFirstName,
        inspector.lastName AS inspectorLastName,
        r.roomNumber,
        r.roomName,
        camp.campusShortName
    FROM DeviceBorrowRecord br
    INNER JOIN Device d ON br.deviceID = d.deviceID
    INNER JOIN User u ON br.borrowUserCNA = u.CNA
    LEFT JOIN DeviceReturnRecord rr ON br.borrowRecordID = rr.borrowRecordID
    LEFT JOIN CheckDeviceReturnRecord c ON rr.checkRecordID = c.checkRecordID
    LEFT JOIN User inspector ON c.inspector = inspector.CNA
    INNER JOIN Room r ON d.roomID = r.roomID
    INNER JOIN Campus camp ON r.campusID = camp.campusID
    WHERE (? IS NULL OR u.CNA = ?)  -- Fixed column name and parameter
      AND br.borrowDate >= ?
      AND (
        (? = TRUE AND rr.borrowRecordID IS NOT NULL) OR  -- Explicit boolean handling
        (? = FALSE AND rr.borrowRecordID IS NULL))
    ORDER BY br.borrowDate DESC
""".trimIndent()

                // Execute query using JDBC or Exposed framework
            return@APIprocess jdbcTemplate.query(
                query, arrayOf<Any?>( // Use arrayOf<Any?> to handle nulls
                    targetCNA,
                    targetCNA,
                    borrowDateAfter,
                    returned,
                    returned
                )) { rs, _ ->
                    BorrowListResponse.BorrowRecord(
                        borrowRecordID = rs.getInt("borrowRecordID"),
                        borrowDate = rs.getDate("borrowDate").toLocalDate(),
                        leasePeriod = rs.getDate("leasePeriod").toLocalDate(),
                        deviceID = rs.getInt("deviceID"),
                        deviceName = rs.getString("deviceName"),
                        borrowerCNA = rs.getString("borrowerCNA"),
                        borrowerFirstName = rs.getString("borrowerFirstName"),
                        borrowerLastName = rs.getString("borrowerLastName"),
                        returnDate = rs.getDate("returnDate")?.toLocalDate(),
                        checkDate = rs.getTimestamp("checkDate")?.toLocalDateTime(),
                        inspectorCNA = rs.getString("inspectorCNA"),
                        inspectorFirstName = rs.getString("inspectorFirstName"),
                        inspectorLastName = rs.getString("inspectorLastName"),
                        roomNumber = rs.getString("roomNumber"),
                        roomName = rs.getString("roomName"),
                        campusShortName = rs.getString("campusShortName")
                    )
                }
        } as List<BorrowListResponse.BorrowRecord>

    }

    fun checkReturn(
        CNA: String,
        RFIDList: List<String>
    ): CheckReturnResponse {
        // 1. 按 deviceID 分組 RFID 並驗證零件完整性
        val deviceGroups = validateRFIDs(RFIDList)

        // 2. 插入總檢查記錄
        val checkRecordId = insertCheckRecord(CNA)

        // 3. 為每個設備更新歸還記錄
        updateDeviceReturnRecords(deviceGroups.keys, checkRecordId)

        // 4. 構建返回結果
        return CheckReturnResponse(
            checkedDevice = buildCheckedList(deviceGroups)
        )
    }

    // ==== 核心逻辑函数 ====
    private fun validateRFIDs(
        RFIDList: List<String>
    ): Map<Int, Set<Int>> {
        if (RFIDList.isEmpty()) throw IllegalArgumentException("RFID 列表不能为空")

        // 查询所有有效 RFID 对应的设备与零件
        val rfidInfo = jdbcTemplate.query("""
        SELECT dr.deviceID, dr.devicePartID 
        FROM DeviceRFID dr 
        WHERE dr.RFID IN (${RFIDList.joinToString(",") { "?" }})
    """, RFIDList.toTypedArray()) { rs, _ ->
            rs.getInt("deviceID") to rs.getInt("devicePartID")
        }

        if (rfidInfo.isEmpty()) throw IllegalArgumentException("无有效的 RFID")

        // 按 deviceID 分组并验证每个设备的零件完整性
        return rfidInfo.groupBy { it.first }.mapValues { (deviceId, parts) ->
            val existingParts = parts.map { it.second }.toSet()
            val requiredParts = jdbcTemplate.queryForList(
                "SELECT devicePartID FROM DevicePart WHERE deviceID = ?",
                Int::class.java, deviceId
            ).toSet()

            if (existingParts != requiredParts) {
                throw IllegalArgumentException("设备 $deviceId 零件不齐，缺少部件: ${requiredParts - existingParts}")
            }
            existingParts
        }
    }

    private fun insertCheckRecord(CNA: String): Int {
        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(
                "INSERT INTO CheckDeviceReturnRecord (checkDT, inspector) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS  // 關鍵設定
            )
            ps.setObject(1, LocalDateTime.now())
            ps.setString(2, CNA)
            ps
        }, keyHolder)

        return keyHolder.key?.toInt() ?: throw IllegalStateException("獲取檢查記錄 ID 失敗")
    }

    private fun updateDeviceReturnRecords(
        deviceIds: Set<Int>,
        checkRecordId: Int
    ) {
        deviceIds.forEach { deviceId ->
            jdbcTemplate.update("""
            UPDATE DeviceReturnRecord drr
            JOIN DeviceBorrowRecord dbr ON drr.borrowRecordID = dbr.borrowRecordID
            SET drr.checkRecordID = ?
            WHERE dbr.deviceID = ?
            AND drr.checkRecordID IS NULL
        """.trimIndent(), checkRecordId, deviceId
            )
        }
    }

    private fun buildCheckedList(
        deviceGroups: Map<Int, Set<Int>>
    ): List<CheckReturnResponse.CheckedDevice> {
        return deviceGroups.keys.map { deviceId ->
            val deviceName = jdbcTemplate.queryForObject(
                "SELECT deviceName FROM Device WHERE deviceID = ?",
                arrayOf(deviceId),
                String::class.java
            ) ?: "未知設備"

            CheckReturnResponse.CheckedDevice(
                deviceID = deviceId,
                deviceName = deviceName,
                partsChecked = true  // 已通過驗證
            )
        }
    }



}