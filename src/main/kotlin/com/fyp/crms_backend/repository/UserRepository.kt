package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


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

        CNA = jdbcTemplate.queryForObject<String>(
            """SELECT CNA FROM usercard WHERE CardID = ?""",
            arrayOf(CardID)
        )


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

    fun renewToken(CNA: String, salt: String, ipAddress: String): CAMSDB.User? {
        return super.APIprocess(CNA, "renew token in ip $ipAddress") {
            var user: CAMSDB.User? = null
            user = jdbcTemplate.queryForObject(
                """SELECT * FROM user where CNA = ? and lastLoginIP = ? and salt = ? and password like '0%'""",
                arrayOf(CNA, ipAddress, salt)
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
            return@APIprocess user
        } as CAMSDB.User?
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun changePw(CNA: String, oldPW: String, newPW: String): Boolean {
        return super.APIprocess(CNA, "Change Password") {
            var state = false
            val newSalt = getRandomString(5)

            // Verify the old password
            val user = jdbcTemplate.query(
                """SELECT * 
               FROM user 
               WHERE CNA = ? 
                 AND SUBSTRING(password, 2) = SHA2(CONCAT(?, (SELECT salt FROM user WHERE CNA = ?)), 256)""",
                rowMapper,
                CNA, oldPW, CNA
            ).firstOrNull()

            if (user == null) {
                // If the old password is incorrect, process an error
                throw super.errorProcess("E08") // E08: Invalid old password
            } else {
                // Update the password with the new salt and new password
                val rowsUpdated = jdbcTemplate.update(
                    """UPDATE user 
                   SET password = CONCAT('0', SHA2(CONCAT(?, ?), 256)), 
                       salt = ?
                   WHERE CNA = ?""",
                    newPW, newSalt, newSalt, CNA
                )

                // Check if the update was successful
                state = rowsUpdated > 0
            }

            return@APIprocess state
        } as Boolean
    }




    fun addUser(
        CNA: String,
        cnaToInsert: String,
        emailDomain: String?,
        password: String,
        accessLevel: Int,
        firstName: String,
        lastName: String,
        contentNo: String,
        campusID: Int
    ): Boolean {
        return super.APIprocess(CNA, "Add User Data") {
            val count = jdbcTemplate.queryForObject(
                """SELECT COUNT(*) FROM user WHERE CNA = ?""",
                Int::class.java,
                cnaToInsert
            ) ?: 0

            // If user exists, throw an exception.
           return@APIprocess if (count > 0) {
                throw RuntimeException("User already exists")
            }else {

                val salt = getRandomString(5)
                val accessPage: Int = when (accessLevel) {
                    0 -> 65535
                    100 -> 63487
                    1000 -> 1540
                    else -> 0
                }

               // In order to show fail log, I put the transactional tag here. by Danteh
               @Transactional
                fun performInsert(){
                    val rowsUpdated = jdbcTemplate.update(
                        """INSERT INTO user (CNA, emailDomain, salt, password, accessLevel, accessPage, firstName, lastName, contentNo, campusID, loginFail)
                   VALUES (?, ?, ?, CONCAT('0', SHA2(CONCAT(?, ?), 256)), ?, ?, ?, ?, ?, ?, 0)""",
                        cnaToInsert,
                        emailDomain,
                        salt,
                        password,
                        salt,
                        accessLevel,
                        accessPage,
                        firstName,
                        lastName,
                        contentNo,
                        campusID
                    )
                    if (rowsUpdated > 0) {
                        true
                    } else {
                        throw RuntimeException("Failed to add user")
                    }
                }

                performInsert()

            }
        } as Boolean
    } // end add user



} // end class