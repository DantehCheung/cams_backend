package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Response

data class GetItemByRFIDResponse(
    val deviceID: Int,
    val deviceName: String,
    val roomID: Int,
    val deviceState: String,
    val remark: String,
    val devicePartID: Int,
    val devicePartName: String,
):Response
