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

    // CREATE
    fun addData(campusShortName: String, campusName: String): String {
        // Check for existing record
        val count = jdbcTemplate.queryForObject(
            """
        SELECT COUNT(*) FROM campus
        WHERE campusShortName = ? OR campusName = ?
        """.trimIndent(),
            Int::class.java,
            campusShortName,
            campusName
        ) ?: 0

        return if (count > 0) {
            "Campus already exists"
        } else {
            val rows = jdbcTemplate.update(
                """
            INSERT INTO campus(campusShortName, campusName)
            VALUES (?, ?)
            """.trimIndent(),
                campusShortName,
                campusName
            )
            if (rows > 0) "Inserted successfully" else "No rows inserted"
        }
    }

    // UPDATE

    fun editData(campusID:Int, campusShortName: String, campusName: String): String {
        val count = jdbcTemplate.queryForObject(
            """
        SELECT COUNT(*) FROM campus
        WHERE campusID = ?
        """.trimIndent(),
            Int::class.java,
           campusID
        ) ?: 0

        return if (count > 0) {
            val rows = jdbcTemplate.update(
                """
            UPDATE campus
            SET campusName = ?,
                campusShortName = ?
            WHERE campusID = ?
            """.trimIndent(),
                campusName,
                campusShortName,
                campusID
            )
            if (rows > 0) "Updated successfully" else "No rows updated"
        } else {
            "Campus does not exist"
        }
    }

}