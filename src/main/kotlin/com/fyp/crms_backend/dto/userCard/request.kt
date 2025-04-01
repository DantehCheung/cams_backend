package com.fyp.crms_backend.dto.userCard

import com.fyp.crms_backend.dto.Request

data class AddUserCardRequest(
    val CardID: String,
    val CNA: String,
    val token: String
) : Request

data class EditUserCardRequest(
    val CardID: String,
    val newCardID: String? = null,
    val state: String? = null,
    val token: String
) : Request

data class DeleteUserCardRequest(
    val CardID: String,
    val token: String
) : Request