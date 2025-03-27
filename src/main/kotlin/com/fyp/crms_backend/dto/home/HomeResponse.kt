package com.fyp.crms_backend.dto.home

import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.entity.CAMSDB
import java.math.BigDecimal
import java.time.LocalDate

data class HomeResponse(
    val LastLoginTime:String,
    val LastLoginPlace:String,
    val PendingConfirmItem:List<PC>
) : Response

data class PC(
    val deviceID: Int? = 0,
    val deviceName: String? = null,
    val price: BigDecimal? = null,
    val orderDate: LocalDate? = null,
    val roomID: Int? = null,
    val state: Char? = null,
    val remark: String? = null
)

