package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Request

data class LoginRequest(
    val CNA: String,
    val password: String
) : Request
