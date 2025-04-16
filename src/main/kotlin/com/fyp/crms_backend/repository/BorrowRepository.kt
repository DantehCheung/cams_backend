package com.fyp.crms_backend.repository

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.borrow.BorrowListResponse
import com.fyp.crms_backend.dto.borrow.CheckReturnResponse
import com.fyp.crms_backend.dto.borrow.RemandResponse
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Repository
class BorrowRepository(jdbcTemplate: JdbcTemplate, snowflake: Snowflake) :
    ApiRepository(jdbcTemplate, snowflake) {
    val MAX_LOAN_DAYS = 30

    private fun checkBookingAvailable(
        itemID: Int,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): Boolean {
        return try {
            // 1. 檢查設備基礎狀態
            val deviceState = jdbcTemplate.queryForObject(
                "SELECT state FROM Device WHERE deviceID = ?",
                arrayOf(itemID),
                String::class.java
            ) ?: return false

            // 僅允許在ARLE狀態下預訂
            if (deviceState !in listOf("A", "R", "L", "E")) return false

            // 2. 檢查時間衝突
            val conflictCount = jdbcTemplate.queryForObject(
                """
            SELECT COUNT(*) 
            FROM DeviceBorrowRecord br
            LEFT JOIN DeviceReturnRecord rr ON br.borrowRecordID = rr.borrowRecordID
            WHERE br.deviceID = ?
            AND (
                COALESCE(rr.returnDate, br.leasePeriod) >= ?  -- 使用實際歸還日期或租期結束日期
                AND br.borrowDate <= ?
            )
            """,
                arrayOf(itemID, startDate, endDate ?: startDate.plusDays(14)),
                Int::class.java
            ) ?: 0

            conflictCount == 0
        } catch (e: EmptyResultDataAccessException) {
            false  // 設備不存在
        }
    }

    private fun checkBorrowAvailable(CNA: String, itemID: Int): Boolean {
        return try {
            // 1. 檢查設備基礎狀態
            val (deviceState, reservedBy) = jdbcTemplate.queryForObject(
                """
            SELECT d.state, br.borrowUserCNA 
            FROM Device d
            LEFT JOIN DeviceBorrowRecord br 
              ON d.deviceID = br.deviceID 
              AND br.borrowRecordID = (
                  SELECT MAX(borrowRecordID) 
                  FROM DeviceBorrowRecord 
                  WHERE deviceID = d.deviceID
              )
            WHERE d.deviceID = ?
            """,
                arrayOf(itemID)
            ) { rs, _ ->
                rs.getString("state") to rs.getString("borrowUserCNA")
            } ?: return false

            // 2. 狀態驗證
            when (deviceState) {
                "A" -> false  // 可借出狀態直接允許
                "R" -> {
                    // 檢查預留人是否為當前用戶
                    reservedBy == CNA
                }

                else -> throw IllegalStateException("This device is not allow to be borrowed") // 其他狀態不允許借出
            }
        } catch (e: EmptyResultDataAccessException) {
            false  // 設備不存在
        }
    }

    private fun checkReturnAvailable(itemID: Int): Boolean {
        return try {
            // 查詢設備狀態
            val state = jdbcTemplate.queryForObject(
                "SELECT state FROM Device WHERE deviceID = ?",
                arrayOf(itemID),
                String::class.java
            )

            // 只有當狀態為 'L' (借出中) 時允許歸還
            state == "L"
        } catch (e: EmptyResultDataAccessException) {
            // 設備不存在時返回 false
            false
        }
    }

    private fun checkEndDate(startDate: LocalDate, endDate: LocalDate) {
        if (startDate > endDate) {
            throw RuntimeException("The start date should not be later than the end date")
        }
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
        if (daysBetween > MAX_LOAN_DAYS) {
            throw RuntimeException("The loan period should not exceed $MAX_LOAN_DAYS days")
        }
    }

    @Transactional
    fun reservationSQL(
        CNA: String,
        itemID: Int,
        borrowDate: LocalDate,
        endDate: LocalDate? = null
    ): Boolean {
        // 1. 插入借閱記錄
        val insertResult = jdbcTemplate.update(
            """
        INSERT INTO DeviceBorrowRecord 
        (borrowDate, deviceID, borrowUserCNA, leasePeriod)
        VALUES (?, ?, ?, ?)
        """,
            borrowDate, itemID, CNA, endDate ?: borrowDate.plusDays(14)
        )
        if (borrowDate == LocalDate.now()) {
            // 2. 更新設備狀態為借出（L）
            jdbcTemplate.update(
                "UPDATE Device SET state = 'L' WHERE deviceID = ?",
                itemID
            )
        }

        return insertResult > 0
    }

    fun reservation(
        CNA: String,
        itemID: Int,
        borrowDate: LocalDate,
        endDate: LocalDate? = null
    ): Boolean {
        return super.APIprocess(CNA, "reservation") {
            if (endDate != null) {
                checkEndDate(borrowDate, endDate)
            }
            if (!checkBookingAvailable(itemID, borrowDate, endDate)) {
                return@APIprocess false
            }

            return@APIprocess reservationSQL(CNA, itemID, borrowDate, endDate)
        } as Boolean

    }

    @Transactional
    fun borrowSQL(CNA: String, itemID: Int, endDate: LocalDate? = null): Boolean {
        val result: Int
        if (endDate != null) {
            result = jdbcTemplate.update(
                """INSERT INTO `deviceborrowrecord` (`deviceID`, `borrowUserCNA`, `leasePeriod`) VALUES ( ?, ?, ?)""",
                itemID, CNA, endDate
            )
        } else {
            result = jdbcTemplate.update(
                """INSERT INTO `deviceborrowrecord` (`deviceID`, `borrowUserCNA`) VALUES ( ?, ?)""",
                itemID, CNA
            )
        }
        jdbcTemplate.update(
            """UPDATE device
                    SET state = 'L'
                    WHERE deviceID = ?""".trimIndent(),
            itemID
        )
        return result > 0
    }


    fun borrow(CNA: String, itemID: Int, endDate: LocalDate? = null): Boolean {
        return super.APIprocess(CNA, "borrow") {
            if (checkBorrowAvailable(CNA, itemID)) {
                if (endDate != null) {
                    throw RuntimeException("The Device is not available for borrowing")
                }
                jdbcTemplate.update(
                    """UPDATE device
                    SET state = 'L'
                    WHERE deviceID = ?""".trimIndent(),
                    itemID
                )
                return@APIprocess true
            }
            if (endDate != null) {
                checkEndDate(LocalDate.now(), endDate)
            }


            return@APIprocess borrowSQL(CNA, itemID, endDate)
        } as Boolean
    }


    fun remand(CNA: String, returnList: List<Int>): List<RemandResponse.deviceResult> {

        return super.APIprocess(CNA, "remand") {
            val stateList: List<RemandResponse.deviceResult> = returnList.map { itemID ->
                RemandResponse.deviceResult(itemID = itemID, state = false)
            }
            returnList.map { itemID ->
                if (!checkReturnAvailable(itemID)) {
                    return@map RemandResponse.deviceResult(
                        itemID = itemID,
                        state = false
                    ) // Simply return the default state
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

    fun getBorrowList(
        CNA: String,
        targetCNA: String? = null,
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
    WHERE (? IS NULL OR ? = '' OR u.CNA = ?)  -- Fixed column name and parameter
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
                    targetCNA,
                    borrowDateAfter,
                    returned,
                    returned
                )
            ) { rs, _ ->
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
        if (RFIDList.isEmpty()) throw RuntimeException("RFID 列表不能为空")

        // 查询所有有效 RFID 对应的设备与零件
        val rfidInfo = jdbcTemplate.query(
            """
        SELECT dr.deviceID, dr.devicePartID 
        FROM DeviceRFID dr 
        WHERE dr.RFID IN (${RFIDList.joinToString(",") { "?" }})
    """, RFIDList.toTypedArray()
        ) { rs, _ ->
            rs.getInt("deviceID") to rs.getInt("devicePartID")
        }

        if (rfidInfo.isEmpty()) throw RuntimeException("无有效的 RFID")

        // 按 deviceID 分组并验证每个设备的零件完整性
        return rfidInfo.groupBy { it.first }.mapValues { (deviceId, parts) ->
            val existingParts = parts.map { it.second }.toSet()
            val requiredParts = jdbcTemplate.queryForList(
                "SELECT devicePartID FROM DevicePart WHERE deviceID = ?",
                Int::class.java, deviceId
            ).toSet()

            if (existingParts != requiredParts) {
                throw RuntimeException("设备 $deviceId 零件不齐，缺少部件: ${requiredParts - existingParts}")
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

    @Transactional
    fun updateDeviceReturnRecords(
        deviceIds: Set<Int>,
        checkRecordId: Int
    ) {
        deviceIds.forEach { deviceId ->
            jdbcTemplate.update(
                """
            UPDATE DeviceReturnRecord drr
            JOIN DeviceBorrowRecord dbr ON drr.borrowRecordID = dbr.borrowRecordID
            SET drr.checkRecordID = ?
            WHERE dbr.deviceID = ?
            AND drr.checkRecordID IS NULL
        """.trimIndent(), checkRecordId, deviceId
            )
            jdbcTemplate.update(
                """UPDATE device
                    SET state = 'A'
                    WHERE deviceID = ?""".trimIndent(),
                deviceId
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