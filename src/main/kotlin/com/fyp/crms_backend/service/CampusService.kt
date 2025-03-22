package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.campus.CampusAddRequest
import com.fyp.crms_backend.dto.campus.CampusAddResponse
import com.fyp.crms_backend.dto.campus.CampusRequest
import com.fyp.crms_backend.dto.campus.CampusResponse
import com.fyp.crms_backend.repository.CampusRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class CampusService(private val campusRepository: CampusRepository, jwt: JWT) : ApiService(jwt) {

    fun execute(request: CampusRequest): CampusResponse {

        val data: Claims = decryptToken(request.token)

        val repo = campusRepository.fetchData(data.subject)
            ?: throw IllegalArgumentException("No campus data found for the user")



        return CampusResponse(
            campusId = repo.campusID!!,
            campusShortName = repo.campusShortName!!,
            campusName = repo.campusName!!


        )
    }

    fun add(request: CampusAddRequest): CampusAddResponse {
        val data: Claims = decryptToken(request.token)
        val result: String = campusRepository.addData(request.campusShortName, request.campusName)
        return CampusAddResponse(
            campusShortName = request.campusShortName,
            campusName = request.campusName,
            resultState = result
        )
    }


}


