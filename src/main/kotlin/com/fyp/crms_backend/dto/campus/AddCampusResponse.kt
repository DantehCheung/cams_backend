package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Response

data class AddCampusResponse(
    val campusName: String? = null,
    val campusShortName: String? = null,
    val resultState: String? = null
) : Response


