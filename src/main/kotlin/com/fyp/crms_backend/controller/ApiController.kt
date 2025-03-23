package com.fyp.crms_backend.controller

import com.fyp.crms_backend.dto.Request
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.error.ErrorResponse
import com.fyp.crms_backend.exception.ErrorCodeException
import org.springframework.web.bind.annotation.RequestBody

open class ApiController {
    fun process(@RequestBody request: Request, main: () -> Response): Response {
        try {
            return main()
        } catch (e: ErrorCodeException) {
            return ErrorResponse(e.errorCode.name, e.errorCode.description)
        } catch (e: Exception) {
            return ErrorResponse("E00", e.message.toString())
        }
    }
}