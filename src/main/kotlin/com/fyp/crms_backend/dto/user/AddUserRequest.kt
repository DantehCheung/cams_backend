package com.fyp.crms_backend.dto.user

import com.fyp.crms_backend.dto.Request

data class AddUserRequest(
    val token: String,
    val CNA: String,
    val emailDomain:String? = "stu.vtc.edu.hk",
    val password: String,
    val accessLevel: Int,
    val firstName: String,
    val lastName: String,
    val contentNo: String,
    val campusID: Int,
) : Request