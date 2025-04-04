package com.fyp.crms_backend.utils

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.exception.ErrorCodeException
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate

open class Logger(protected open val jdbcTemplate: JdbcTemplate,
                  private val idGenerator: Snowflake
) {

    protected fun addLog(log: String): Boolean {
        return try {
            val sql = "INSERT INTO log (ID, userCNA, log) VALUES (?, null, ?)"
            val id = idGenerator.nextId()
            jdbcTemplate.update(sql, id, log)
            true
        } catch (e: DataAccessException) {
            println(e.message)
            false
        }
    }

    protected fun addLog(CNA: String?, log: String): Boolean {
        return try {
            val sql = "INSERT INTO log (ID, userCNA, log) VALUES (?, ?, ?)"
            val id = idGenerator.nextId()
            jdbcTemplate.update(sql, id, CNA, log)
            true
        } catch (e: DataAccessException) {
            println(e.message)
            false
        }
    }
    protected fun errorProcess(code: String): ErrorCodeException {
        return ErrorCodeException(ErrorCode.toErrorCode(code))
    }
}