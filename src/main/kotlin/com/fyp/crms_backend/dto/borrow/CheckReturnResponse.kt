package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Response

data class CheckReturnResponse(val checkedDevice:List<CheckedDevice>): Response {
    data class CheckedDevice(
        val deviceID: Int,
        val deviceName: String,
        val partsChecked: Boolean
    )
}
