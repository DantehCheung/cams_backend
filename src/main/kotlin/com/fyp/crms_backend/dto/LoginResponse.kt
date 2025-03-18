package com.fyp.crms_backend.dto

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val accessLevel: Int,
    val accessPage: Int,
    val firstName: String,
    val lastName: String
)
