package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.exception.ErrorCodeException
import com.fyp.crms_backend.utils.ErrorCode
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Logger
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
abstract class ApiService(val jwt: JWT, jdbcTemplate: JdbcTemplate): Logger(jdbcTemplate, Snowflake(1, 3)) {



    protected fun decryptToken(token: String): Claims {
        // Simulate token verification logic
        return if (token.isNotEmpty()) {
            try {
                jwt.decrypteToken(token)
            } catch (e: Exception) {
                //TODO:換token
                throw e
            }

//            CNA = data.subject
//            println("ApiRepository - checkToken")
//            println("Subject: ${data.subject}")
//            println("Access Level: ${data["accessLevel"]}")
//            println("Issued At: ${data.issuedAt}")
//            println("Expiration: ${data.expiration}")

        } else {
            throw IllegalArgumentException("Invalid token")

        }
    }

//    abstract fun execute(request: Request): Response

}


