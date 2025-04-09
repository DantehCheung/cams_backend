package com.fyp.crms_backend.dto.user

import com.fyp.crms_backend.dto.Request

data class AddUserRequest(
    val token: String,
    val userList: List<User>
) : Request {
    data class User(
        val CNA: String,
        val emailDomain: String,
        val password: String,
        val accessLevel: Int,
        val accessPage: Int? = null,
        val firstName: String,
        val lastName: String,
        val contentNo: String,
        val campusID: Int,
    )
}