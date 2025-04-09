package com.fyp.crms_backend.utils

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.exception.ErrorCodeException
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate

open class Logger(
    protected open val jdbcTemplate: JdbcTemplate,
    private val idGenerator: Snowflake
) {

    protected fun addLog(log: String): Boolean {
        return addLog(null, log)
    }

    protected fun addLog(CNA: String?, log: String): Boolean {
        return addLog(CNA, log, 0)
    }

    private fun addLog(CNA: String?, log: String, i: Int): Boolean {
        return try {
            if (i > 5) {
                println("try addLog, fail 5 times with CNA = $CNA \nmsg = $log")
                return false
            }
            val sql = "INSERT INTO log (ID, userCNA, log) VALUES (?, ?, ?)"
            val id = idGenerator.nextId()
            jdbcTemplate.update(sql, id, CNA, log)

            true
        } catch (e: DuplicateKeyException) {
            addLog(CNA, log, i + 1)
        } catch (e: DataAccessException) {
            println(e.message)
            false
        }
    }

    protected fun errorProcess(code: String): ErrorCodeException {
        return ErrorCodeException(ErrorCode.toErrorCode(code))
    }
}