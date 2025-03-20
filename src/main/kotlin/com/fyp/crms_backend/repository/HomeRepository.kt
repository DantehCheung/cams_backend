package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class HomeRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.User> { rs, _ ->
        CAMSDB.User(
            lastLoginTime = rs.getTimestamp("lastLoginTime")?.toLocalDateTime(),
            lastLoginIP = rs.getString("lastLoginIP"),
        )
    }

    fun fetchData(CNA: String): CAMSDB.User? {

        return super.APIprocess(CNA, arrayOf(), "get Home Data") {
                val result: List<CAMSDB.User> = jdbcTemplate.query(
                    """SELECT lastLoginTime, lastLoginIP FROM user WHERE CNA = ? """,
                    rowMapper,
                    CNA
                )

                return@APIprocess result.firstOrNull()
        } as CAMSDB.User?

    }


}