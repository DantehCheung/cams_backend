package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class RoomRepository(override val jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

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
}