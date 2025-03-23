package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Request

data class ChangePwRequest(
    val token: String,
    val oldPassword: String,
    val newPassword: String
) : Request
