package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request


data class ManualInventoryRequest(
    val token: String,
    val manualInventoryLists: List<String>,
    val roomID: Int
) : Request

