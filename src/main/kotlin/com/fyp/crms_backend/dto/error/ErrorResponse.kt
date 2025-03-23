package com.fyp.crms_backend.dto.error

import com.fyp.crms_backend.dto.Response

data class ErrorResponse(
    val errorCode: String,
    val description: String
) : Response