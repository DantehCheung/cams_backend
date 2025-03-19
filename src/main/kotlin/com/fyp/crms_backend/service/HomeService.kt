package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.HomeRequest
import com.fyp.crms_backend.dto.HomeResponse
import com.fyp.crms_backend.repository.HomeRepository
import org.springframework.stereotype.Service


@Service
class HomeService(private val homeRepository: HomeRepository) {


    fun execute(request: HomeRequest): HomeResponse {
        val repo = homeRepository.fetchData(request.token)
            ?: throw IllegalArgumentException("null")


        return HomeResponse(
            LastLoginTime = repo.lastLoginTime!!.toString(),
            LastLoginPlace = repo.lastLoginIP!!
        )
    }

}


