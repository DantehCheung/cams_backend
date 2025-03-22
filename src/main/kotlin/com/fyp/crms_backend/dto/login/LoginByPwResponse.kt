package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Response

data class LoginByPwResponse(
    val token: String,
    val refreshToken: String,
    val accessLevel: Int,
    val accessPage: Int,
    val firstName: String,
    val lastName: String,
    val lastLoginIp: String
) : Response
