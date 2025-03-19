package com.fyp.crms_backend.repository

import com.fyp.crms_backend.utils.ErrorCode
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper


abstract class ApiRepository(protected val jdbcTemplate: JdbcTemplate, private val jwt: JWT) {

    protected var CNA: String = ""

    // Check if the token is valid
    private fun checkToken(token: String): Boolean {
        // Simulate token verification logic
        return if (token.isNotEmpty()) {
            val data: Claims = jwt.decrypteToken(token)
            CNA = data.subject
            println("ApiRepository - checkToken")
            println("Subject: ${data.subject}")
            println("Access Level: ${data["accessLevel"]}")
            println("Issued At: ${data.issuedAt}")
            println("Expiration: ${data.expiration}")
            true
        } else {
            false
        }
    }

    // Check if the arguments are valid
    private fun checkArg(args: Array<out Any?>): Boolean {
        // Example: Ensure no argument is null
        return args.isNotEmpty() && args.all { arg -> arg != null }
    }

    private fun checkPermissions():Boolean{
        return true
    }

    // Add a log entry to the database
    private fun addLog(CNA: String, log: String): Boolean {
        return try {
            val sql = "INSERT INTO log (DT,userCNA, log) VALUES (NOW(), ?, ?)"
            jdbcTemplate.update(sql, CNA, log)
            true
        } catch (e: DataAccessException) {
            false // Return false if the database connection or query fails
        }
    }

    private fun errorProcess(code:String):String{
        return ErrorCode.toErrorCode(code).toJson()
    }

    // Process API request
    fun APIprocess(
        token: String,
        args: Array<out Any?>,
        log: String,
        main: (args: Array<out Any?>) -> Any?
    ): Any? {
        return try {
            // Step 1: Check token validity
            if (!checkToken(token)) {
                return errorProcess("E04")
            }

            // Step 2: Check argument validity
            if (!checkArg(args)) {
                return errorProcess("E01") // Arguments missing or invalid
            }

            // Step 3: Execute the main process
            val result = main(args)

            // Step 4: Add log on success
            val logAdded = addLog(
                CNA, "successful: $log with ${
                    args.joinToString { arg ->
                        when (arg) {
                            is RowMapper<*> -> "RowMapper instance"
                            else -> arg?.toString() ?: "null"
                        }
                    }
                }"
            )

            if (!logAdded) {
                return errorProcess("E05") // Database connection or query error
            }

            // Step 5: Return the result
            return result
        } catch (e: Exception) {
            // Generic error handler (e.g., unexpected exceptions)
            val logAdded = addLog(CNA, "fail: $log with $args")
            if (!logAdded) {
                return errorProcess("E05") // Database connection or query error
            }
            errorProcess("E06")
        }
    }

    override fun toString(): String {
        return "Repository: ${super.toString()}"
    }
}
