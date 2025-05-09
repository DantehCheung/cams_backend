package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Request

data class AddCampusRequest(
    val campusName: String,
    val campusShortName: String,
    val token: String
) : Request