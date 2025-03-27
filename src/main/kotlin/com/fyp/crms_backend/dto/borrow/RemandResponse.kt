package com.fyp.crms_backend.dto.borrow

import com.fyp.crms_backend.dto.Response

data class RemandResponse(
    val returnStatus:List<deviceResult>
):Response{
    data class deviceResult(
        val itemID:Int,
        var state:Boolean
    )

}