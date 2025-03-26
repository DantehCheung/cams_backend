package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.StateResponse
import com.fyp.crms_backend.dto.borrow.BorrowRequest
import com.fyp.crms_backend.dto.borrow.RemandRequest
import com.fyp.crms_backend.dto.borrow.RemandResponse
import com.fyp.crms_backend.dto.borrow.ReservationRequest
import com.fyp.crms_backend.repository.BorrowRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service

@Service
class BorrowService(private val borrowRepository: BorrowRepository, jwt: JWT) : ApiService(jwt) {

    fun reservation(request: ReservationRequest): Response {
        val data: Claims = decryptToken(request.token)

        val result: Boolean = borrowRepository.reservation(data.subject,request.itemID, request.borrowRecordID)
        return StateResponse(
            status = result
        )
    }

    fun borrow(request: BorrowRequest): Response {
        val data: Claims = decryptToken(request.token)

        val result: Boolean = borrowRepository.borrow(data.subject,request.itemID)
        return StateResponse(
            status = result
        )
    }


    fun remand(request: RemandRequest): Response {
        val data: Claims = decryptToken(request.token)
        val result: List<Boolean> = borrowRepository.remand(data.subject, request.returnList)
        val results: List<RemandResponse.deviceResult> = request.returnList.zip(result).map { (itemID, state) ->
            RemandResponse.deviceResult(itemID = itemID, state = state)
        }

        return RemandResponse(
            returnStatus = results
        )
    }
}