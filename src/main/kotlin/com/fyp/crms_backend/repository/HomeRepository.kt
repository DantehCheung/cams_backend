package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.User
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper


@Repository
class HomeRepository(private val jdbcTemplate: JdbcTemplate):{
    private val rowMapper = RowMapper<User> {
        rs, _ -> User(
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

    fun findByCNAAndPassword(CNA: String, password: String, ipAddress: String): User? {
        val user = jdbcTemplate.query(
            """
            SELECT * 
            FROM user 
            WHERE CNA = ? 
              AND password = '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c'
            """,
            rowMapper,
            CNA
        )
        if (user.isNotEmpty()) {
            jdbcTemplate.update(
                """
            UPDATE user
            SET lastLoginIP = ?
            WHERE CNA = ?
            """,
                ipAddress,
                CNA
            )
        }

        return if (user.isEmpty()) null else user[0]
    }
}