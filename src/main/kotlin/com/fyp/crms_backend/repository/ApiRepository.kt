package com.fyp.crms_backend.repository

import com.fyp.crms_backend.utils.ErrorCode
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.dao.DataAccessException

class ApiRepository(private val jdbcTemplate: JdbcTemplate) {

    // Check if the token is valid
    private fun checkToken(token: String): Boolean {
        // Simulate token verification logic
        return token.isNotEmpty() && token == "valid_token" // Replace with actual token verification logic
    }

    // Check if the arguments are valid
    private fun checkArg(args: Array<out Any>): Boolean {
        // Example: Ensure no argument is null
        return args.isNotEmpty() && args.all { it != null }
    }

    private fun checkPermissions():Boolean{
        return true
    }

    // Add a log entry to the database
    private fun addLog(CNA: String, log: String): Boolean {
        return try {
            val sql = "INSERT INTO logs (DT,userCNA, log) VALUES (NOW(), ?, ?)"
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
        main: (args: Array<out Any>) -> String,
        args: Array<out Any>
    ): String {
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
            val CNA = args.getOrNull(0) as? String ?: return errorProcess("E02")
            val logAdded = addLog(CNA, "Operation successful: $result")
            if (!logAdded) {
                return errorProcess("E05") // Database connection or query error
            }

            // Step 5: Return the result
            return result
        } catch (e: Exception) {
            // Generic error handler (e.g., unexpected exceptions)
            errorProcess("E06")
        }
    }
}
