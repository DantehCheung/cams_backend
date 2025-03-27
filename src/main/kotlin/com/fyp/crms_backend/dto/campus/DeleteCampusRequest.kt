package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Request

data class DeleteCampusRequest(
    val token: String,
    val campusID: Int
): Request