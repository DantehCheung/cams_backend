package com.fyp.crms_backend.dto.campus

import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.entity.CAMSDB

data class GetCampusResponse(
    val c: List<Campus>

) : Response {
    data class Campus(
        val campusId: Int? = null,
        val campusName: String? = null,
        val campusShortName: String? = null,
    )
}
