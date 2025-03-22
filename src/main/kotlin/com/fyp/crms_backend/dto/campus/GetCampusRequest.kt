package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Request

data class GetCampusRequest(
    val token: String
) : Request
