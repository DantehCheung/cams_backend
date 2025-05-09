package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.home.HomeRequest
import com.fyp.crms_backend.dto.home.HomeResponse
import com.fyp.crms_backend.repository.HomeRepository
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class HomeService(
    private val homeRepository: HomeRepository, jwt: JWT, jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {

    fun execute(request: HomeRequest): HomeResponse {

        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER))

        val repo = homeRepository.fetchData(data.subject)

        return HomeResponse(
            LastLoginTime = repo.LastLoginTime.toString(),
            LastLoginPlace = repo.LastLoginPlace,
            PendingConfirmItem = repo.PendingConfirmItem
        )
    }


}


