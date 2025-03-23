package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository


@Repository
class UserRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.User> { rs, _ ->
        CAMSDB.User(
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

    fun findByCNAAndPassword(CNA: String, password: String, ipAddress: String): CAMSDB.User? {
        return super.APIprocess(CNA, "login by pw in ip: $ipAddress") {
            val users = jdbcTemplate.query(
                """SELECT * 
            FROM user 
            WHERE CNA = ? 
              AND password = CONCAT('0', SHA2(CONCAT(?, (SELECT salt FROM user WHERE CNA = ?)), 256))
            """,
                rowMapper,
                CNA, password, CNA
            )
            val user = users.firstOrNull()
            if (user == null) {
                val fuser = jdbcTemplate.query(
                    """SELECT * 
            FROM user 
            WHERE CNA = ?
            """,
                    rowMapper,
                    CNA
                ).firstOrNull()
                if (fuser == null) {
                    throw super.errorProcess("E07")
                } else if (fuser.loginFail!! >= 10) {
                    jdbcTemplate.update(
                        """UPDATE user SET lastLoginIP = ?,loginFail = loginFail +1, password = CONCAT('!', SUBSTRING(password, 2)) WHERE CNA = ?""",
                        ipAddress,
                        CNA
                    )
                } else {
                    jdbcTemplate.update(
                        """UPDATE user SET lastLoginIP = ?,loginFail = loginFail +1 WHERE CNA = ?""",
                        ipAddress,
                        CNA
                    )
                }
                throw errorProcess("E07")
            } else {
                jdbcTemplate.update(
                    """UPDATE user SET lastLoginIP = ?,lastLoginTime = now(),loginFail = 0 WHERE CNA = ?""",
                    ipAddress,
                    CNA
                )
            }


            return@APIprocess user
        } as CAMSDB.User?
    }

    fun findByCard(CardID: String, ipAddress: String): CAMSDB.User? {
        var CNA: String? = null
        try {
            CNA = jdbcTemplate.queryForObject<String>(
                """SELECT CNA FROM usercard WHERE CardID = ?""",
                arrayOf(CardID)
            )
        } catch (e: Exception) {
            throw super.errorProcess("E02")
        }

        return super.APIprocess(CNA!!, "login by Card in ip $ipAddress") {
            val user = jdbcTemplate.query(
                """SELECT * 
            FROM user 
            WHERE CNA = ?
            """,
                rowMapper,
                CNA
            )
            return@APIprocess user.firstOrNull()
        } as CAMSDB.User?

    }

    fun renewToken(CNA: String, ipAddress: String): CAMSDB.User? {
        return super.APIprocess(CNA, "renew token in ip $ipAddress") {
            var user: CAMSDB.User? = null
            try {
                user = jdbcTemplate.queryForObject(
                    """SELECT * FROM user where CNA = ? and lastLoginIP = ?""",
                    arrayOf(CNA, ipAddress)
                ) { rs, _ ->
                    CAMSDB.User(
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
            } catch (e: Exception) {
                throw super.errorProcess("E02")
            }
            return@APIprocess user
        } as CAMSDB.User?
    }

}