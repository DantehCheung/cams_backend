package com.fyp.crms_backend.dto.home

import com.fyp.crms_backend.dto.Response

data class HomeResponse(
    val LastLoginTime:String,
    val LastLoginPlace:String
) : Response
