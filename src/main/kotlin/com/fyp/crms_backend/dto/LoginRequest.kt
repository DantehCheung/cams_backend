package com.fyp.crms_backend

data class LoginRequest(
    val CNA: String,
    val password: String,
    val ip: String
)
