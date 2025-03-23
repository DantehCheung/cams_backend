package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Request

data class RenewTokenRequest(
    val refreshToken: String
) : Request
