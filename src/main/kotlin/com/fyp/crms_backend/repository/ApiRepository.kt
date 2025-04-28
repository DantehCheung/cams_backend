package com.fyp.crms_backend.repository

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.exception.ErrorCodeException
import com.fyp.crms_backend.utils.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate


abstract class ApiRepository(
    jdbcTemplate: JdbcTemplate,
    @Qualifier("snowflakeDatacenter3") snowflake: Snowflake
) : Logger(jdbcTemplate, snowflake) {

    // Check if the arguments are valid
    private fun checkArg(args: Array<out Any?>): Boolean {
        // Example: Ensure no argument is null
        return args.all { arg -> arg != null }
    }


//    fun APIprocess(
//        CNA: String,
//        args: Array<out Any?> = arrayOf(),
//        logMsg: String,
//        main: (args: Array<out Any?>) -> Any?
//    ): Any? {
//        return try {
//            // Step 2: Check argument validity
//            if (!checkArg(args)) {
//                throw errorProcess("E01") // Arguments missing or invalid
//            }
//
//
//            // Step 3: Execute the main process
//            val result = main(args)
//
//            // Step 4: Add log on success
//            val logAdded = addLog(
//                CNA, "successful: $logMsg"
//            )
//
//
//
//            if (!logAdded) {
//                println("API success, but add log fail")
//                throw errorProcess("E05") // Database connection or query error
//            }
//
//            // Step 5: Return the result
//            return result
//        } catch (e: ErrorCodeException) {
//            throw e
//        } catch (e: Exception) {
//            // Generic error handler (e.g., unexpected exceptions)
//            val logAdded = addLog(
//                CNA, "fail: $logMsg with (${e.message})\n${
//                    args.joinToString { arg ->
//                        when (arg) {
//                            is RowMapper<*> -> "RowMapper instance"
//                            else -> arg?.toString() ?: "null:${arg!!::class.simpleName}"
//                        }
//                    }
//                }")
//
//            if (!logAdded) {
//                println("API and add log fail")
//                throw errorProcess("E05") // Database connection or query error
//            }
//            println(e.message)
//            throw errorProcess("E06")
//
//        }
//    }

    fun APIprocess(
        CNA: String,
        logMsg: String,
        main: () -> Any?
    ): Any? {
        return try {

            // Step 3: Execute the main process
            val result = main()

            // Step 4: Add log on success
            val logAdded = addLog(
                CNA, "successful: $logMsg"
            )

            if (!logAdded) {
                println("API success, but add log fail")
                throw errorProcess("E05") // Database connection or query error
            }

            // Step 5: Return the result
            result
        } catch (e: ErrorCodeException) {
            throw e
        } catch (e: DuplicateKeyException) {
            println(e.message)
            addErrorLog(CNA, logMsg, e)
            throw errorProcess("E11") // Duplicate key error
        } catch (e: Exception) {
            println(e.message)
            addErrorLog(CNA, logMsg, e)
            throw e
        }


    }

    private fun addErrorLog(CNA: String, logMsg: String, e: Exception) {

        val logAdded = addLog(CNA, "fail: $logMsg with (${e.message?.replace(Regex(" +"), " ")})")

        if (!logAdded) {
            println("API and add log fail")
            throw errorProcess("E05") // Database connection or query error
        }
    }

    override fun toString(): String {
        return "${super.toString()}: Repository"
    }
}
