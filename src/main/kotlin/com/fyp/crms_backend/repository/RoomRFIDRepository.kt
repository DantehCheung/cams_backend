package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.room.GetRoomResponse
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository


@Repository
class RoomRFIDRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

    fun add(CNA: String, roomID: Int, RFID: String): Boolean {
        return APIprocess(CNA, "Add Room RFID") {
            // 檢查 RFID 是否存在
            val count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM RoomRFID WHERE RFID = ?",
                Int::class.java,
                RFID
            ) ?: 0
            if (count > 0) throw RuntimeException("RFID already exists")

            // 插入新紀錄
            return@APIprocess jdbcTemplate.update(
                "INSERT INTO RoomRFID (roomID, RFID) VALUES (?, ?)",
                roomID,
                RFID
            ) > 0
        } as Boolean
    }

    fun edit(CNA: String, RFID: String, newRFID: String?, state: String?): Boolean {
        return APIprocess(CNA, "Edit Room RFID") {
            val updates = mutableListOf<String>()
            val params = mutableListOf<Any>()

            newRFID?.let {
                updates.add("RFID = ?")
                params.add(it)
            }
            state?.let {
                updates.add("state = ?")
                params.add(it)
            }

            if (updates.isEmpty()) throw IllegalArgumentException("No fields to update")
            params.add(RFID)

            val query = "UPDATE RoomRFID SET ${updates.joinToString(", ")} WHERE RFID = ?"
            return@APIprocess jdbcTemplate.update(query, *params.toTypedArray()) > 0
        } as Boolean
    }

    fun delete(CNA: String, RFID: String): Boolean {
        return APIprocess(CNA, "Delete Room RFID") {
            return@APIprocess jdbcTemplate.update(
                "UPDATE RoomRFID SET state = 'D' WHERE RFID = ?",
                RFID
            ) > 0
        } as Boolean
    }

    fun getRoomByRFID(CNA: String, RFID: String): GetRoomResponse.SingleRoomResponse? {
        return APIprocess(CNA, "Get Room By RFID") {
            return@APIprocess try {
                jdbcTemplate.queryForObject(
                    "SELECT r.roomID, r.campusID, r.roomNumber, r.roomName FROM room r inner join roomrfid rf on r.roomID = rf.roomID where r.state !='D' and rf.state !='D' and rf.RFID = ?",
                    { rs, _ ->
                        GetRoomResponse.SingleRoomResponse(
                            rs.getInt("roomID"),
                            rs.getInt("campusID"),
                            rs.getString("roomNumber"),
                            rs.getString("roomName")
                        )
                    },
                    RFID
                )
            } catch (e: EmptyResultDataAccessException) {
                null
            }
        } as GetRoomResponse.SingleRoomResponse?
    }
}