package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.borrow.*
import com.fyp.crms_backend.repository.BorrowRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class BorrowService(private val borrowRepository: BorrowRepository, jwt: JWT, jdbcTemplate: JdbcTemplate) : ApiService(jwt,jdbcTemplate) {

    fun reservation(request: ReservationRequest): Response {
        val data: Claims = decryptToken(request.token)

        val result: Boolean = borrowRepository.reservation(data.subject, request.itemID, request.borrowRecordDate, request.endDate)
        return StateResponse(
            status = result
        )
    }

    fun borrow(request: BorrowRequest): Response {
        val data: Claims = decryptToken(request.token)

        val result: Boolean = borrowRepository.borrow(data.subject,request.itemID,request.endDate)
        return StateResponse(
            status = result
        )
    }


    fun remand(request: RemandRequest): Response {
        val data: Claims = decryptToken(request.token)
        val results: List<RemandResponse.deviceResult> = borrowRepository.remand(data.subject, request.returnList)


        return RemandResponse(
            returnStatus = results
        )
    }

    fun getBorrowList(request: BorrowListRequest): Response {
        val data: Claims = decryptToken(request.token)
        val results: List<BorrowListResponse.BorrowRecord> =
            borrowRepository.getBorrowList(data.subject, request.targetCNA, request.borrowDateAfter, request.returned)

        return BorrowListResponse(
            borrowRecord = results
        )
    }

    fun checkReturn(request: CheckReturnRequest): Response {
        val data: Claims = decryptToken(request.token)
        val response:CheckReturnResponse = borrowRepository.checkReturn(data.subject,request.RFIDList)

        return response
    }

}