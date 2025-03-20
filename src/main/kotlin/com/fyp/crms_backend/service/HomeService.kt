package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.home.HomeRequest
import com.fyp.crms_backend.dto.home.HomeResponse
import com.fyp.crms_backend.repository.HomeRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class HomeService(private val homeRepository: HomeRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: HomeRequest): HomeResponse {

        val data: Claims = decryptToken(request.token)

        val repo = homeRepository.fetchData(data.subject)
            ?: throw IllegalArgumentException("null")

        return HomeResponse(
            LastLoginTime = repo.lastLoginTime!!.toString(),
            LastLoginPlace = repo.lastLoginIP!!
        )
    }

}


