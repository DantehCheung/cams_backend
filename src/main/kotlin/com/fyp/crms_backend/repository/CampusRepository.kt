package com.fyp.crms_backend.repository

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class CampusRepository(jdbcTemplate: JdbcTemplate, snowflake: Snowflake) :
    ApiRepository(jdbcTemplate, snowflake) {
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

    fun fetchData(CNA: String, accessLevel: Int): List<CAMSDB.Campus> {

        return super.APIprocess(CNA, "get Campus Data") {
            val result: List<CAMSDB.Campus> = if (accessLevel > 100) {
                jdbcTemplate.query(
                    """SELECT campus.campusID, campusShortName, campusName 
             FROM user 
             JOIN campus ON user.campusID = campus.campusID 
             WHERE CNA = ? and campus.state = 'A'""", rowMapper, CNA
                )
            } else {
                jdbcTemplate.query("""SELECT * FROM cams.campus WHERE state = 'A'""", rowMapper)
            }


            return@APIprocess result
        } as List<CAMSDB.Campus>

    }

    // CREATE
    fun addData(CNA: String, campusShortName: String, campusName: String): String {

        return super.APIprocess(CNA, "add Campus Data") {
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

            return@APIprocess if (count > 0) {
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
        } as String

    }

    // UPDATE

    fun editData(CNA: String, campusID: Int, campusShortName: String, campusName: String): String {
        return super.APIprocess(CNA, "edit Campus Data") {
            val count = jdbcTemplate.queryForObject(
                """
        SELECT COUNT(*) FROM campus
        WHERE campusID = ?
        """.trimIndent(),
                Int::class.java,
                campusID
            ) ?: 0

            return@APIprocess if (count > 0) {
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
                throw RuntimeException("Campus does not exist")

            }
        } as String
    }

    @Transactional
    // DELETE
    fun deleteData(CNA: String, campusID: Int): Boolean {
        return super.APIprocess(CNA, "delete Campus Data") {
            val count = jdbcTemplate.queryForObject(
                """SELECT COUNT(*) FROM campus WHERE campusID = ? """,
                Int::class.java,
                campusID
            ) ?: 0

            return@APIprocess if (count > 0) {
                val rows = jdbcTemplate.update(
                    """UPDATE campus SET state = 'D' WHERE campusID = ?""",
                    campusID
                )
                rows > 0
            } else {
                throw RuntimeException("Campus does not exist")
            }

        } as Boolean
    }

}