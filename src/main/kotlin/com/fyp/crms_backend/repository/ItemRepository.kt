package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.item.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate


@Repository
class ItemRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {


    fun fetchData(CNA: String, roomID: Int): GetItemResponse {
        return super.APIprocess(CNA, "get campus") {
            val sqlDevices = """
        SELECT * 
        FROM Device 
        WHERE roomID = ?
    """

            // Fetch devices
            val devices: List<GetItemResponse.Devices> = jdbcTemplate.query(sqlDevices, arrayOf(roomID)) { rs, _ ->
                GetItemResponse.Devices(
                    deviceID = rs.getInt("deviceID"),
                    deviceName = rs.getString("deviceName"),
                    price = rs.getBigDecimal("price"),
                    orderDate = rs.getDate("orderDate")?.toLocalDate(),
                    arriveDate = rs.getDate("arriveDate")?.toLocalDate(),
                    maintenanceDate = rs.getDate("maintenanceDate")?.toLocalDate(),
                    roomID = rs.getInt("roomID"),
                    state = rs.getString("state")?.firstOrNull(), // Get the first character of the ENUM
                    remark = rs.getString("remark"),
                    docs = fetchDeviceDocs(rs.getInt("deviceID")),
                    partID = fetchDeviceParts(rs.getInt("deviceID")),
                    deviceRFID = fetchDeviceRFIDs(rs.getInt("deviceID"))
                )
            }

            return@APIprocess GetItemResponse(device = devices)
        } as GetItemResponse
    }

    fun fetchDeviceDocs(deviceID: Int): List<GetItemResponse.DeviceDoc> {
        val sql = """
        SELECT * 
        FROM DeviceDoc 
        WHERE deviceID = ?
    """
        return jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
            GetItemResponse.DeviceDoc(
                deviceID = rs.getInt("deviceID"),
                docPath = rs.getString("docPath")
            )
        }
    }

    fun fetchDeviceParts(deviceID: Int): List<GetItemResponse.DevicePartID> {
        val sql = """
        SELECT * 
        FROM DevicePart 
        WHERE deviceID = ?
    """

        return jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
            GetItemResponse.DevicePartID(
                deviceID = rs.getInt("deviceID"),
                devicePartID = rs.getInt("devicePartID"),
                devicePartName = rs.getString("devicePartName")
            )
        }
    }

    fun fetchDeviceRFIDs(deviceID: Int): List<GetItemResponse.DeviceRFID> {
        val sql = """
        SELECT * 
        FROM DeviceRFID 
        WHERE deviceID = ?
    """

        return jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
            GetItemResponse.DeviceRFID(
                deviceID = rs.getInt("deviceID"),
                devicePartID = rs.getInt("devicePartID"),
                RFID = rs.getString("RFID")
            )
        }
    }


    private fun fetchDeviceDocs(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DeviceDoc>> {
        return jdbcTemplate.query(
            """SELECT deviceID, docPath FROM deviceDoc WHERE deviceID IN (${deviceIDs.joinToString()})"""
        ) { rs, _ ->
            GetItemResponse.DeviceDoc(
                deviceID = rs.getInt("deviceID"),
                docPath = rs.getString("docPath")
            )
        }.groupBy { it.deviceID }
    }

    private fun fetchDeviceParts(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DevicePartID>> {
        return jdbcTemplate.query(
            """
        SELECT 
            deviceID, 
            devicePartID, 
            devicePartName 
        FROM devicePartID 
        WHERE deviceID IN (${deviceIDs.joinToString()})
        """
        ) { rs, _ ->
            GetItemResponse.DevicePartID(
                deviceID = rs.getInt("deviceID"),
                devicePartID = rs.getInt("devicePartID"),
                devicePartName = rs.getString("devicePartName")
            )
        }.groupBy { it.deviceID }
    }

    private fun fetchDeviceRFIDs(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DeviceRFID>> {
        return jdbcTemplate.query(
            """
        SELECT 
            deviceID, 
            devicePartID, 
            RFID            
        FROM deviceRFID 
        WHERE deviceID IN (${deviceIDs.joinToString()})
        """
        ) { rs, _ ->
            GetItemResponse.DeviceRFID(
                deviceID = rs.getInt("deviceID"),
                devicePartID = rs.getInt("devicePartID"),
                RFID = rs.getString("RFID")
            )
        }.groupBy { it.deviceID }
    }


    // Brief explanation:
// 1. For each device, insert into 'device' and retrieve the new ID.
// 2. Then insert docs and parts (and RFIDs) referencing that new ID.
    @Transactional
    fun addItem(CNA: String, devices: List<DeviceWithParts>): Boolean {
        return super.APIprocess(CNA, "add Device Data") {
            devices.forEach { device ->
                val deviceId = addSingleDevice(device)
                addDocs(deviceId, device.deviceDoc)
                addParts(deviceId, device.deviceParts)
            }
            return@APIprocess true
        } as Boolean
    }

    private fun addSingleDevice(device: DeviceWithParts): Int {
        val keyHolder = org.springframework.jdbc.support.GeneratedKeyHolder()
        /*在插入資料時，GeneratedKeyHolder 會自動接收資料庫產生的主鍵 (常見為自增 ID)，並儲存在 keyHolder 中。
        接著可透過 keyHolder.key 或對應方法取得該新插入記錄的 ID 來進行後續操作。*/
        jdbcTemplate.update(
            { con ->
                val ps = con.prepareStatement(
                    """INSERT INTO device (deviceName, price, orderDate, arriveDate, maintenanceDate, roomID, state, remark)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
                    java.sql.Statement.RETURN_GENERATED_KEYS
                )
                ps.setString(1, device.deviceName)
                ps.setBigDecimal(2, device.price)
                ps.setObject(3, device.orderDate)
                ps.setObject(4, device.arriveDate)
                ps.setObject(5, device.maintenanceDate)
                ps.setInt(6, device.roomID)
                ps.setString(7, device.state.toString())
                ps.setString(8, device.remark)
                ps // Return the PreparedStatement
            },
            keyHolder
        )
        return keyHolder.key?.toInt() ?: throw IllegalStateException("No generated key returned")
    }

    private fun addDocs(deviceId: Int, docs: List<DeviceDoc>) {
        docs.forEach { doc ->
            jdbcTemplate.update(
                """INSERT INTO DeviceDoc (deviceID, docPath) VALUES (?, ?)""",
                deviceId,
                doc.docPath
            )
        }
    }

    private fun addParts(deviceId: Int, parts: List<DevicePart>) {
        parts.forEach { part ->
            // Insert first without generating the key automatically
            jdbcTemplate.update(
                { con ->
                    val ps = con.prepareStatement(
                        """INSERT INTO DevicePart (deviceID, devicePartName)
                       VALUES (?, ?)"""
                    )
                    ps.setInt(1, deviceId)
                    ps.setString(2, part.devicePartName)
                    ps
                }
            )

            // Query to retrieve the partID for the inserted record.
            // This assumes that devicePartName is unique for this device.
            val partId = jdbcTemplate.queryForObject(
                """SELECT devicePartID FROM DevicePart
               WHERE deviceID = ? AND devicePartName = ?""",
                Int::class.java,
                deviceId,
                part.devicePartName
            ) ?: throw IllegalStateException("No generated key returned")

            addRFIDs(deviceId, partId, part.deviceRFID)
        }
    }

    private fun addRFIDs(deviceID: Int, partId: Int, rfids: List<DeviceRFID>) {
        rfids.forEach { rfid ->
            jdbcTemplate.update({ con ->
                val ps = con.prepareStatement(
                    """INSERT INTO DeviceRFID (deviceID, devicePartID, RFID) VALUES (?, ?, ?)"""
                )
                ps.setInt(1, deviceID)
                ps.setInt(2, partId)
                ps.setString(3, rfid.RFID)
                ps
            })
        }
    }

// ---------------------------------------------------------------------------------------------
    // Delete Item
@Transactional
fun deleteItem(CNA: String, deviceID: Int): Boolean {
    return super.APIprocess(CNA, "delete Device Data") {
        // Verify that the device exists and is not already marked as deleted.
        val count = jdbcTemplate.queryForObject(
            """SELECT COUNT(1) FROM Device WHERE deviceID = ? AND state <> 'D'""",
            Int::class.java,
            deviceID
        ) ?: 0

        if (count == 0) {
            throw IllegalStateException("Device not found or already deleted")
        }

        // Perform delete operations (mark as deleted)
        jdbcTemplate.update(
            """UPDATE Device SET state = 'D' WHERE deviceID = ?""",
            deviceID
        )
        jdbcTemplate.update(
            """UPDATE DevicePart SET state = 'D' WHERE deviceID = ?""",
            deviceID
        )
        jdbcTemplate.update(
            """UPDATE DeviceRFID SET state = 'D' WHERE deviceID = ?""",
            deviceID
        )
        jdbcTemplate.update(
            """UPDATE DeviceDoc SET state = 'D' WHERE deviceID = ?""",
            deviceID
        )

        return@APIprocess true
    } as Boolean
}

    // Edit device (big)
    fun editItem(
        CNA: String, deviceID: Int, deviceName: String, price: BigDecimal, orderDate: LocalDate, arriveDate: LocalDate,
        maintenanceDate: LocalDate, roomID: Int, state: Char, remark: String, docs: List<UpdatedDeviceDoc>
    ): Boolean {

        return super.APIprocess(CNA, "editData") {

            // Verify that the device exists and is not already marked as deleted.
            val devicecount = jdbcTemplate.queryForObject(
                """SELECT COUNT(1) FROM Device WHERE deviceID = ?""",
                Int::class.java,
                deviceID
            ) ?: 0

            if (devicecount == 0) {
                throw IllegalStateException("Device not found or already deleted")
            }

            val roomcount = jdbcTemplate.queryForObject(
                """SELECT COUNT(1) FROM Room WHERE roomID = ?""", Int::class.java,
                roomID
            ) ?: 0

            if (roomcount == 0) {
                throw IllegalStateException("Room not found")
            }

            val rowUpdate = jdbcTemplate.update(
                """UPDATE Device SET deviceName = ?, price = ?, orderDate = ?,
            | arriveDate = ?, maintenanceDate = ?, roomID = ?, state = ?, remark = ? 
            | WHERE deviceID = ?""".trimMargin(),
                deviceName, price, orderDate, arriveDate, maintenanceDate, roomID, state, remark, deviceID
            )

            if (rowUpdate == 0) {
                throw IllegalStateException("Update Device not success")
            }

            var docsUpdate: Int = 0

            docs.forEach { doc ->
                // Update document
                val docUpdate: Int = jdbcTemplate.update(
                    { con ->
                        val ps = con.prepareStatement(
                            """UPDATE deviceDoc SET docPath = ?, state = ? WHERE deviceID = ?"""
                        )
                        ps.setString(1, doc.docPath)
                        ps.setString(2, doc.state.toString())
                        ps.setInt(3, deviceID)
                        ps
                    }
                )
                if (docUpdate == 0) {
                    throw IllegalStateException("Update Device doc not success")
                } else {
                    docsUpdate = docsUpdate + 1
                }
            }

            return@APIprocess if (rowUpdate > 0 && docsUpdate == docs.size) {
                true
            } else {
                false
            }
        } as Boolean
    }



    //Manual Adjust Item
    data class DeviceStateInfo(
        val deviceID: Int,
        val deviceName: String,
        val currentState: Char,
        val RFID: String
    )

    fun getDeviceStatesByRFIDs(roomID: Int, rfids: List<String>): List<DeviceStateInfo> {
        val sql = """
        SELECT d.deviceID, d.deviceName, d.state, dr.RFID
        FROM Device d
        JOIN DeviceRFID dr ON d.deviceID = dr.deviceID
        WHERE d.roomID = ? 
          AND dr.RFID IN (${rfids.joinToString { "?" }})
          AND d.state != 'D'
    """

        // Prepare the arguments: roomID first, followed by the RFIDs
        val args = arrayOf(roomID, *rfids.toTypedArray())

        // Execute the query with the arguments and a row mapper
        return jdbcTemplate.query(sql, args) { rs, _ ->
            DeviceStateInfo(
                deviceID = rs.getInt("deviceID"),
                deviceName = rs.getString("deviceName"),
                currentState = rs.getString("state").first(),
                RFID = rs.getString("RFID")
            )
        }
    }


    @Transactional
    fun batchUpdateDeviceStates(updates: Map<Int, Char>): Map<Int, Char> {
        val afterStates = mutableMapOf<Int, Char>()

        updates.forEach { (deviceID, newState) ->
            jdbcTemplate.update(
                "UPDATE Device SET state = ? WHERE deviceID = ?",
                newState.toString(),
                deviceID
            )


            val afterState = jdbcTemplate.queryForObject(
                "SELECT state FROM Device WHERE deviceID = ?",
                Char::class.java,
                deviceID
            ) ?: 'E'

            afterStates[deviceID] = afterState
        }

        return afterStates
    }

    fun getRoomRFIDInfo(roomID: Int): List<DeviceStateInfo> {
        val sql = """
        SELECT d.deviceID, d.deviceName, d.state, dr.RFID
        FROM Device d
        JOIN DeviceRFID dr ON d.deviceID = dr.deviceID
        WHERE d.roomID = ? 
          AND d.state != 'D'
          AND dr.state = 'A'
    """
        return jdbcTemplate.query(sql, arrayOf(roomID)) { rs, _ ->
            DeviceStateInfo(
                rs.getInt("deviceID"),
                rs.getString("deviceName"),
                rs.getString("state").first(),
                rs.getString("RFID")
            )
        }
    }
}