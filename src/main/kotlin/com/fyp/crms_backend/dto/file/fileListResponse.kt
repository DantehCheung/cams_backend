package com.fyp.crms_backend.dto.file

import com.fyp.crms_backend.dto.Response

data class fileListResponse(
    val files: List<String>
) : Response