package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import com.fyp.crms_backend.utils.JWT
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class HomeRepository(jdbcTemplate: JdbcTemplate, jwt: JWT) : ApiRepository(jdbcTemplate, jwt) {
    private val rowMapper = RowMapper<CAMSDB.User> { rs, _ ->
        CAMSDB.User(
            lastLoginTime = rs.getTimestamp("lastLoginTime")?.toLocalDateTime(),
            lastLoginIP = rs.getString("lastLoginIP"),
        )
    }

    fun fetchData(token: String): CAMSDB.User? {
        try {
            val sql: String = """SELECT lastLoginTime, lastLoginIP FROM user WHERE CNA = ? """
            return super.APIprocess(token, arrayOf(rowMapper, sql), "get Home Data") {
                val result: List<CAMSDB.User> = jdbcTemplate.query(
                    sql,
                    rowMapper,
                    CNA
                )

                return@APIprocess result.firstOrNull()
            } as? CAMSDB.User?
        } catch (e: Exception) {
            println("Error while processing token: ${e.message}")
            return null
        }
    }


}