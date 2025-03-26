package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.item.*
import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository



@Repository
class ItemRepository(override val jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

    private val rowMapper = RowMapper<CAMSDB.Device> { rs, _ ->
        CAMSDB.Device(
         deviceID = rs.getInt("deviceID"),
         deviceName = rs.getString("deviceName"),
         price = rs.getBigDecimal("price"),
         orderDate = rs.getDate("orderDate")?.toLocalDate(),
         arriveDate = rs.getDate("arriveDate")?.toLocalDate(),
         maintenanceDate = rs.getDate("maintenanceDate")?.toLocalDate(),
         roomID = rs.getInt("roomID"),
         state = rs.getString("state")?.singleOrNull(),
         remark = rs.getString("remark"),
        )
    }

    fun fetchData(CNA: String,roomID: Int): List<CAMSDB.Device> {

        return super.APIprocess(CNA, "get Device Data") {

            val result: List<CAMSDB.Device> = jdbcTemplate.query(
                """SELECT deviceID,deviceName,price,orderDate,arriveDate,maintenanceDate,device.roomID,state,remark from device,room,user where  device.roomID = room.roomID AND user.CNA = ? AND room.roomID = ?""",
                rowMapper,
                CNA,
                roomID
            )

            return@APIprocess result
        } as List<CAMSDB.Device>

    }


    // Brief explanation:
// 1. For each device, insert into 'device' and retrieve the new ID.
// 2. Then insert docs and parts (and RFIDs) referencing that new ID.

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
                        """INSERT INTO DevicePartID (deviceID, devicePartName)
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
                """SELECT devicePartID FROM DevicePartID 
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



}