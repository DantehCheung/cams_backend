package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.campus.GetCampusRequest
import com.fyp.crms_backend.dto.campus.GetCampusResponse
import com.fyp.crms_backend.repository.CampusRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class CampusService(private val campusRepository: CampusRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: GetCampusRequest): GetCampusResponse {

        val data: Claims = decryptToken(request.token)

        val repo = campusRepository.fetchData(data.subject)
            ?: throw IllegalArgumentException("No campus data found for the user")



        return GetCampusResponse(
            campusId = repo.campusID!!,
            campusShortName = repo.campusShortName!!,
            campusName = repo.campusName!!


        )
    }


}


