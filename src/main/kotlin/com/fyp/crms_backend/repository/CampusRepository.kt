package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class CampusRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.Campus> { rs, _ ->
        CAMSDB.Campus(
            campusID = rs.getInt("campusID"),
            campusShortName = rs.getString("campusShortName"),
            campusName = rs.getString("campusName"),
        )
    }

    private val rowMapper1 = RowMapper<CAMSDB.User> { rs, _ ->
        CAMSDB.User(
            accessLevel = rs.getInt("accessLevel"),
        )
    }

    fun fetchData(CNA: String): List<CAMSDB.Campus> {

        return super.APIprocess(CNA, "get Campus Data") {
            val result1 = jdbcTemplate.query(
                """SELECT accessLevel FROM user where CNA = ?""",
                rowMapper1,
                CNA
            )
            val accessLevel:Int = result1.firstOrNull()?.accessLevel!!

            val result: List<CAMSDB.Campus> = if (accessLevel > 100) {
                jdbcTemplate.query("""SELECT campus.campusID, campusShortName, campusName 
             FROM user 
             JOIN campus ON user.campusID = campus.campusID 
             WHERE CNA = ?""", rowMapper, CNA)
            } else {
                jdbcTemplate.query("""SELECT * FROM cams.campus""", rowMapper)
            }


            return@APIprocess result
        } as List<CAMSDB.Campus>

    }


}