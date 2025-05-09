package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.userCard.AddUserCardRequest
import com.fyp.crms_backend.dto.userCard.DeleteUserCardRequest
import com.fyp.crms_backend.dto.userCard.EditUserCardRequest
import com.fyp.crms_backend.repository.UserCardRepository
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class UserCardService(
    private val userCardRepository: UserCardRepository,
    jwt: JWT, jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {

    fun add(request: AddUserCardRequest): StateResponse {
        val data: Claims = decryptToken(request.token,Permission.ADMIN)
        val result = userCardRepository.add(data.subject, request.CardID, request.CNA)
        return StateResponse(result)
    }

    fun edit(request: EditUserCardRequest): StateResponse {
        val data: Claims = decryptToken(request.token,Permission.ADMIN)
        val result = userCardRepository.edit(
            data.subject,
            request.CardID,
            request.newCardID,
            request.state
        )
        return StateResponse(result)
    }

    fun delete(request: DeleteUserCardRequest): StateResponse {
        val data: Claims = decryptToken(request.token,Permission.ADMIN)
        val result = userCardRepository.delete(data.subject, request.CardID)
        return StateResponse(result)
    }
}