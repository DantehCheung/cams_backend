package com.fyp.crms_backend.repository

import com.fyp.crms_backend.exception.ErrorCodeException
import com.fyp.crms_backend.utils.ErrorCode
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper


abstract class ApiRepository(protected open val jdbcTemplate: JdbcTemplate) {


    // Check if the arguments are valid
    private fun checkArg(args: Array<out Any?>): Boolean {
        // Example: Ensure no argument is null
        return args.all { arg -> arg != null }
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

    fun errorProcess(code: String): ErrorCodeException {
        return ErrorCodeException(ErrorCode.toErrorCode(code))
    }


    fun APIprocess(
        CNA: String,
        args: Array<out Any?> = arrayOf(),
        logMsg: String,
        main: (args: Array<out Any?>) -> Any?
    ): Any? {
        return try {
            // Step 2: Check argument validity
            if (!checkArg(args)) {
                throw errorProcess("E01") // Arguments missing or invalid
            }

            if (!checkPermissions()) {
                throw errorProcess("E03")
            }

            // Step 3: Execute the main process
            val result = main(args)

            // Step 4: Add log on success
            val logAdded = addLog(
                CNA, "successful: $logMsg"
            )



            if (!logAdded) {
                throw errorProcess("E05") // Database connection or query error
            }

            // Step 5: Return the result
            return result
        } catch (e: ErrorCodeException) {
            throw e
        } catch (e: Exception) {
            // Generic error handler (e.g., unexpected exceptions)
            val logAdded = addLog(
                CNA, "fail: $logMsg with (${e.message})\n${
                    args.joinToString { arg ->
                        when (arg) {
                            is RowMapper<*> -> "RowMapper instance"
                            else -> arg?.toString() ?: "null:${arg!!::class.simpleName}"
                        }
                    }
                }")

            if (!logAdded) {
                throw errorProcess("E05") // Database connection or query error
            }
            throw errorProcess("E06")
        }
    }

    fun APIprocess(
        CNA: String,
        logMsg: String,
        main: () -> Any?
    ): Any? {
        return try {

            if (!checkPermissions()) {
                throw errorProcess("E03")
            }

            // Step 3: Execute the main process
            val result = main()

            // Step 4: Add log on success
            val logAdded = addLog(
                CNA, "successful: $logMsg"
            )



            if (!logAdded) {
                throw errorProcess("E05") // Database connection or query error
            }

            // Step 5: Return the result
            return result
        } catch (e: ErrorCodeException) {
            throw e
        } catch (e: Exception) {
            // Generic error handler (e.g., unexpected exceptions)
            val logAdded = addLog(CNA, "fail: $logMsg with (${e.message})")

            if (!logAdded) {
                throw errorProcess("E05") // Database connection or query error
            }
            throw errorProcess("E06")
        }
    }

    override fun toString(): String {
        return "${super.toString()}: Repository"
    }
}
