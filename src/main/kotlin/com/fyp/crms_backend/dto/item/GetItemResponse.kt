package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Response
import java.math.BigDecimal
import java.time.LocalDate

data class GetItemResponse(
    val deviceID: Int,
    val deviceName: String,
    val price: BigDecimal,
    val orderDate: LocalDate,
    val arriveDate: LocalDate,
    val maintenanceDate: LocalDate,
    val roomID: Int,
    val state: Char,
    val remark: String

    ) : Response
