package com.fyp.crms_backend.controller.borrow

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.borrow.BorrowListRequest
import com.fyp.crms_backend.dto.borrow.BorrowRequest
import com.fyp.crms_backend.dto.borrow.CheckReturnRequest
import com.fyp.crms_backend.dto.borrow.RemandRequest
import com.fyp.crms_backend.dto.borrow.ReservationRequest
import com.fyp.crms_backend.service.BorrowService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/br")
class BorrowController(private val borrowService: BorrowService) : ApiController() {
    @PostMapping("/reservation")
    fun reservation(
        @RequestBody request: ReservationRequest
    ): Response {
        return process(request) {
            return@process borrowService.reservation(request)
        }
    }

    @PostMapping("/borrow")
    fun borrow(
        @RequestBody request: BorrowRequest
    ): Response {
        return process(request) {
            return@process borrowService.borrow(request)
        }
    }

    @PostMapping("/return")
    fun remand(
        @RequestBody request: RemandRequest
    ): Response {
        return process(request) {
            return@process borrowService.remand(request)
        }
    }

    @PostMapping("/getborrowrecord")
    fun getborrowRecord(@RequestBody request: BorrowListRequest): Response {
        return process(request) {
            return@process borrowService.getBorrowList(request)
        }
    }


    @PostMapping("/check")
    fun check(@RequestBody request: CheckReturnRequest): Response {
        return process(request) {
            return@process borrowService.checkReturn(request)
        }
    }


}