package com.fyp.crms_backend.dto.home

import com.fyp.crms_backend.dto.Request

data class HomeRequest(
    val token: String
) : Request
