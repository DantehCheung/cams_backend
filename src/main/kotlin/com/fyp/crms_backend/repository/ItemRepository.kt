package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.item.DeviceDoc
import com.fyp.crms_backend.dto.item.DevicePart
import com.fyp.crms_backend.dto.item.DeviceRFID
import com.fyp.crms_backend.dto.item.DeviceWithParts
import com.fyp.crms_backend.dto.item.EditItemRequest
import com.fyp.crms_backend.dto.item.GetItemResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class ItemRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {


    fun fetchData(CNA: String, roomID: Int): GetItemResponse {
        return super.APIprocess(CNA, "get device") {
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
        return super.APIprocess("fetchDeviceDocs", "fetch device documents") {
            val sql = """
                SELECT *
                FROM DeviceDoc
                WHERE deviceID = ?
            """
            jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
                GetItemResponse.DeviceDoc(
                    deviceID = rs.getInt("deviceID"),
                    docPath = rs.getString("docPath")
                )
            }
        } as List<GetItemResponse.DeviceDoc>
    }

    fun fetchDeviceParts(deviceID: Int): List<GetItemResponse.DevicePartID> {
        return super.APIprocess("fetchDeviceParts", "fetch device parts") {
            val sql = """
                SELECT *
                FROM DevicePart
                WHERE deviceID = ?
            """
            jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
                GetItemResponse.DevicePartID(
                    deviceID = rs.getInt("deviceID"),
                    devicePartID = rs.getInt("devicePartID"),
                    devicePartName = rs.getString("devicePartName")
                )
            }
        } as List<GetItemResponse.DevicePartID>
    }

    fun fetchDeviceRFIDs(deviceID: Int): List<GetItemResponse.DeviceRFID> {
        return super.APIprocess("fetchDeviceRFIDs", "fetch device RFIDs") {
            val sql = """
                SELECT *
                FROM DeviceRFID
                WHERE deviceID = ?
            """
            jdbcTemplate.query(sql, arrayOf(deviceID)) { rs, _ ->
                GetItemResponse.DeviceRFID(
                    deviceID = rs.getInt("deviceID"),
                    devicePartID = rs.getInt("devicePartID"),
                    RFID = rs.getString("RFID")
                )
            }
        } as List<GetItemResponse.DeviceRFID>
    }

    private fun fetchDeviceDocs(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DeviceDoc>> {
        return super.APIprocess("fetchDeviceDocs", "fetch multiple device documents") {
            jdbcTemplate.query(
                """SELECT deviceID, docPath FROM deviceDoc WHERE deviceID IN (${deviceIDs.joinToString()})"""
            ) { rs, _ ->
                GetItemResponse.DeviceDoc(
                    deviceID = rs.getInt("deviceID"),
                    docPath = rs.getString("docPath")
                )
            }.groupBy { it.deviceID }
        } as Map<Int?, List<GetItemResponse.DeviceDoc>>
    }

    private fun fetchDeviceParts(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DevicePartID>> {
        return super.APIprocess("fetchDeviceParts", "fetch multiple device parts") {
            jdbcTemplate.query(
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
        } as Map<Int?, List<GetItemResponse.DevicePartID>>
    }

    private fun fetchDeviceRFIDs(deviceIDs: List<Int>): Map<Int?, List<GetItemResponse.DeviceRFID>> {
        return super.APIprocess("fetchDeviceRFIDs", "fetch multiple device RFIDs") {
            jdbcTemplate.query(
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
        } as Map<Int?, List<GetItemResponse.DeviceRFID>>
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
        return super.APIprocess("addSingleDevice", "add single device") {
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
                    ps
                },
                keyHolder
            )
            keyHolder.key?.toInt() ?: throw IllegalStateException("No generated key returned")
        } as Int
    }

    private fun addDocs(deviceId: Int, docs: List<DeviceDoc>) {
        return super.APIprocess("addDocs", "add device documents") {
            docs.forEach { doc ->
                jdbcTemplate.update(
                    """INSERT INTO DeviceDoc (deviceID, docPath) VALUES (?, ?)""",
                    deviceId,
                    doc.docPath
                )
            }
        } as Unit
    }


    private fun addParts(deviceId: Int, parts: List<DevicePart>) {
        return super.APIprocess("addParts", "add device parts") {
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
        } as Unit
    }

    private fun addRFIDs(deviceID: Int, partId: Int, rfids: List<DeviceRFID>) {
        return super.APIprocess("addRFIDs", "add device RFIDs") {
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
        } as Unit
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


    // Edit
    @Transactional
    fun editItem(CNA: String, deviceID: Int, request: EditItemRequest): Boolean {
        return super.APIprocess(CNA, "edit Device Data") {
            // Verify device exists.
            val count = jdbcTemplate.queryForObject(
                """SELECT COUNT(1) FROM Device WHERE deviceID = ?""",
                Int::class.java,
                deviceID
            ) ?: 0

            if (count == 0) {
                throw IllegalStateException("Device not found")
            }
            // Update Device record.
            jdbcTemplate.update(
                """UPDATE Device
                   SET deviceName = ?,
                       price = ?,
                       orderDate = ?,
                       arriveDate = ?,
                       maintenanceDate = ?,
                       roomID = ?,
                       state = ?,
                       remark = ?
                   WHERE deviceID = ?""",
                request.deviceName,
                request.price,
                request.orderDate,
                request.arriveDate,
                request.maintenanceDate,
                request.roomID,
                request.state.toString(),
                request.remark,
                deviceID
            )
            // Update document records.
            request.docs.forEach { doc ->
                jdbcTemplate.update(
                    """UPDATE DeviceDoc SET docPath = ?
                       WHERE deviceDocID = ? AND deviceID = ?""",
                    doc.docPath,
                    doc.deviceDocID,
                    deviceID
                )
            }

            // Update device part and associated RFID records.
            request.deviceParts.forEach { part ->
                jdbcTemplate.update(
                    """UPDATE DevicePart SET devicePartName = ?
                       WHERE devicePartID = ? AND deviceID = ?""",
                    part.devicePartName,
                    part.devicePartID,
                    deviceID
                )
                part.deviceRFID.forEach { rfid ->
                    jdbcTemplate.update(
                        """UPDATE DeviceRFID SET RFID = ?
                           WHERE deviceRFIDID = ? AND deviceID = ?""",
                        rfid.RFID,
                        rfid.deviceRFIDID,
                        deviceID
                    )
                }
            }
            return@APIprocess true
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
        return super.APIprocess("getDeviceStatesByRFIDs", "get device states by RFIDs") {
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
            jdbcTemplate.query(sql, args) { rs, _ ->
                DeviceStateInfo(
                    deviceID = rs.getInt("deviceID"),
                    deviceName = rs.getString("deviceName"),
                    currentState = rs.getString("state").first(),
                    RFID = rs.getString("RFID")
                )
            }
        } as List<DeviceStateInfo>
    }


    @Transactional
    fun batchUpdateDeviceStates(updates: Map<Int, Char>): Map<Int, Char> {
        return super.APIprocess("batchUpdateDeviceStates", "batch update device states") {
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

            afterStates
        } as Map<Int, Char>
    }

    fun getRoomRFIDInfo(roomID: Int): List<DeviceStateInfo> {
        return super.APIprocess("getRoomRFIDInfo", "get room RFID info") {
            val sql = """
                SELECT d.deviceID, d.deviceName, d.state, dr.RFID
                FROM Device d
                JOIN DeviceRFID dr ON d.deviceID = dr.deviceID
                WHERE d.roomID = ?
                  AND d.state != 'D'
                  AND dr.state = 'A'
            """
            jdbcTemplate.query(sql, arrayOf(roomID)) { rs, _ ->
                DeviceStateInfo(
                    rs.getInt("deviceID"),
                    rs.getString("deviceName"),
                    rs.getString("state").first(),
                    rs.getString("RFID")
                )
            }
        } as List<DeviceStateInfo>
    }
}