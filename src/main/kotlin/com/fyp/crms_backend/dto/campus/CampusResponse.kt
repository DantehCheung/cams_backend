package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Response

data class CampusResponse(
    val campusId: Int? = null,
    val campusName: String? = null,
    val campusShortName: String? = null,

    ) : Response
