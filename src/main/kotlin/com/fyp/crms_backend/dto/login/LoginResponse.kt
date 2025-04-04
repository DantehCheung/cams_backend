package com.fyp.crms_backend.dto.login

import com.fyp.crms_backend.dto.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val accessLevel: Int,
    val accessPage: Int,
    val firstName: String,
    val lastName: String,
    val lastLoginIp: String,
    val lastLoginTime: LocalDateTime
) : Response
