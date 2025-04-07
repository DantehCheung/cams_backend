package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.exception.ErrorCodeException
import com.fyp.crms_backend.utils.ErrorCode
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Logger
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
abstract class ApiService(val jwt: JWT, jdbcTemplate: JdbcTemplate) : Logger(jdbcTemplate, Snowflake(1, 3)) {

    // 允許所有人訪問
    protected fun decryptToken(token: String): Claims {
        return handleTokenValidation(token) { /* 無需權限檢查 */ }
    }

    // 允許指定等級範圍訪問
    protected fun decryptToken(
        token: String,
        minAccessLevel: Int = 0,
        maxAccessLevel: Int = Permission.maxLevel()
    ): Claims {
        return handleTokenValidation(token) { claims ->
            val accessLevel = claims.getAccessLevel()
            if (accessLevel !in minAccessLevel..maxAccessLevel) {
                addLog("E03: Access denied. Required level: $minAccessLevel-$maxAccessLevel, Actual: $accessLevel")
                throw errorProcess("E03")
            }
        }
    }

    // 允許指定權限列表訪問
    protected fun decryptToken(token: String, allowList: List<Permission>): Claims {
        return handleTokenValidation(token) { claims ->
            val accessLevel = claims.getAccessLevel()
            val allowedLevels = allowList.map { it.level }

            if (accessLevel !in allowedLevels) {
                val required = allowList.joinToString { it.displayName }
                addLog("E03: Required permissions: $required, Actual level: $accessLevel")
                throw errorProcess("E03")
            }
        }
    }

    private inline fun handleTokenValidation(
        token: String,
        validation: (Claims) -> Unit
    ): Claims {
        return try {
            val claims = jwt.decrypteToken(token)
            validation(claims)
            claims
        } catch (e: Exception) {
            when (e) {
                is ErrorCodeException -> {
                    addLog("${e.errorCode.name}: ${e.errorCode.description}")
                    throw e
                }
                else -> {
                    addLog("E10: Token decryption failed - ${e.message}")
                    throw errorProcess("E10")
                }
            }
        }
    }

    private fun Claims.getAccessLevel(): Int {
        return this["accessLevel"] as? Int
            ?: throw IllegalArgumentException("Missing accessLevel in token")
    }
}


