package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Request
import com.fyp.crms_backend.entity.CAMSDB

data class AddItemRequest(
    val token: String,
    val roomID: Int,
    val devices: List<CAMSDB.Device>
) : Request