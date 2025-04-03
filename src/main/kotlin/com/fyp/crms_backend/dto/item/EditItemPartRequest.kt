package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request

data class EditItemPartRequest(
    val token: String,
    val deviceID: Int,
    val partID: Int,
    val partName: String,
    val state: Char,
) : Request