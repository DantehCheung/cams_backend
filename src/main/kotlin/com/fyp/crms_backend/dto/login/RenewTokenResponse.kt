package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Response

data class RenewTokenResponse(
    val token: String
) : Response
