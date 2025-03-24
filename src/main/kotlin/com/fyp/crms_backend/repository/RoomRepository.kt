package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class RoomRepository(
    override val jdbcTemplate: JdbcTemplate
) : ApiRepository(jdbcTemplate) {

    private val rowMapper = RowMapper<CAMSDB.Room> { rs, _ ->
        CAMSDB.Room(
            roomID = rs.getInt("roomID"),
            campusID = rs.getInt("campusID"),
            roomNumber = rs.getString("roomNumber"),
            roomName = rs.getString("roomName"),
        )
    }

    fun fetchData(CNA: String, campusID: String): List<CAMSDB.Room> {
        println("trying to fetch data room")
        return super.APIprocess(CNA, "get Room Data") {
            jdbcTemplate.query(
                """select roomID, room.campusID,roomNumber,roomName from room, campus where campus.campusID = room.campusID and room.campusID = ?""",
                rowMapper,
                campusID
            )
        } as List<CAMSDB.Room>
    }


    fun addRoom(CNA: String, campusID: Int,roomNumber:String,roomName: String): Boolean{
        return super.APIprocess(CNA, "add Room Data"){
            // Check for existing record
            val count = jdbcTemplate.queryForObject(
                """SELECT COUNT(*) FROM room WHERE roomNumber = ? AND campusID = ?""",
                Int::class.java,
                roomNumber,
                campusID
            ) ?: 0
            return@APIprocess if (count > 0){
                false
            }else {
                val rows =
                jdbcTemplate.update(
                    """INSERT INTO room (campusID, roomNumber,roomName) VALUES (?, ?, ?)""".trimIndent(),
                    campusID,
                    roomNumber,
                    roomName
                )
                if (rows > 0) {
                    true
                } else {
                    false
                }
            }
        } as Boolean
    }


    fun editRoom(CNA:String,campusID:Int,roomID:Int,roomNumber:String,roomName: String): Boolean {
        return super.APIprocess(CNA, "edit Room Data") {
            // Check the campus and room query together does it exist
            val count = jdbcTemplate.queryForObject(
                """SELECT COUNT(*) FROM room WHERE roomID = ? AND campusID = ? """.trimIndent(),
                Int::class.java,
                roomID,
                campusID
            ) ?: 0
            return@APIprocess if (count > 0) {
                val rows = jdbcTemplate.update(
                    """UPDATE room SET roomNumber = ?, roomName = ? WHERE roomID = ? AND campusID = ?""".trimIndent(),
                    roomNumber,
                    roomName,
                    roomID,
                    campusID
                )
                if (rows > 0) {
                    true
                } else {
                    false
                }
            }else{
                throw RuntimeException("Room does not exist")
            }
        } as Boolean // return

    } // editRoom

} // class