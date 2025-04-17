package com.fyp.crms_backend.service


import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.campus.AddCampusRequest
import com.fyp.crms_backend.dto.campus.AddCampusResponse
import com.fyp.crms_backend.dto.campus.DeleteCampusRequest
import com.fyp.crms_backend.dto.campus.EditCampusRequest
import com.fyp.crms_backend.dto.campus.EditCampusResponse
import com.fyp.crms_backend.dto.campus.GetCampusRequest
import com.fyp.crms_backend.dto.campus.GetCampusResponse
import com.fyp.crms_backend.repository.CampusRepository
import com.fyp.crms_backend.utils.JWT
import com.fyp.crms_backend.utils.Permission
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class CampusService(
    private val campusRepository: CampusRepository,
    jwt: JWT,
    jdbcTemplate: JdbcTemplate,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {


    fun get(request: GetCampusRequest): GetCampusResponse {

        val data: Claims = decryptToken(request.token)

        val repo = campusRepository.fetchData(data.subject, data["accessLevel"] as Int)

        val c: List<GetCampusResponse.Campus> = repo.map { campus ->
            GetCampusResponse.Campus(
                campusId = campus.campusID,
                campusShortName = campus.campusShortName,
                campusName = campus.campusName
            )
        }


        return GetCampusResponse(
            c = c
        )
    }

    fun add(request: AddCampusRequest): AddCampusResponse {

        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER)) // decrypt the token and get the CNA

        val result: String = campusRepository.addData(
            data.subject,
            request.campusShortName,
            request.campusName
        ) // put CNA into repo
        return AddCampusResponse(
            campusShortName = request.campusShortName,
            campusName = request.campusName,
            resultState = result
        )
    }


    fun edit(request: EditCampusRequest): EditCampusResponse {

        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER)) // decrypt the token and get the CNA

        val result: String = campusRepository.editData(
            data.subject,
            request.campusID,
            request.campusShortName,
            request.campusName
        )
        return EditCampusResponse(
            campusShortName = request.campusShortName,
            campusName = request.campusName,
            resultState = result
        )
    }

    // Delete Campus
    fun delete(request: DeleteCampusRequest): StateResponse {
        val data: Claims = decryptToken(request.token, listOf(Permission.ADMIN,Permission.TEACHER)) // decrypt the token and get the CNA

        val result: Boolean = campusRepository.deleteData(data.subject, request.campusID)

        return StateResponse(
            result
        )
    }


}


