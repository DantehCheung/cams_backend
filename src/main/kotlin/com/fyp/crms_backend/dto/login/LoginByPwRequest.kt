package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Request

data class LoginByPwRequest(
    val CNA: String,
    val password: String
) : Request
