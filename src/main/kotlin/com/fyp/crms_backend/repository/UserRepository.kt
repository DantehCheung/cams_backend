package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class UserRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.User> {
        rs, _ -> CAMSDB.User(
        CNA = rs.getString("CNA"),
        emailDomain = rs.getString("emailDomain"),
        salt = rs.getString("salt"),
        password = rs.getString("password"),
        accessLevel = rs.getInt("accessLevel"),
        accessPage = rs.getInt("accessPage"),
        firstName = rs.getString("firstName"),
        lastName = rs.getString("lastName"),
        contentNo = rs.getString("contentNo"),
        campusID = rs.getInt("campusID"),
        lastLoginTime = rs.getTimestamp("lastLoginTime")?.toLocalDateTime(),
        lastLoginIP = rs.getString("lastLoginIP"),
        loginFail = rs.getInt("loginFail")
        )
    }

    fun findByCNAAndPassword(CNA: String, password: String,ipAddress:String): CAMSDB.User? {
        return super.APIprocess(CNA, arrayOf(), "login by pw") {
            val user = jdbcTemplate.query(
                """SELECT * 
            FROM user 
            WHERE CNA = ? 
              AND password = CONCAT('0', SHA2(CONCAT(?, (SELECT salt FROM user WHERE CNA = ?)), 256))
            """,
                rowMapper,
                CNA, password, CNA
            )
            jdbcTemplate.update(
                """UPDATE user
            SET lastLoginIP = ?
            WHERE CNA = ?
            """,
                ipAddress,
                CNA
            )


            return@APIprocess user.firstOrNull()
        } as CAMSDB.User?


    }
}