package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Response
import java.math.BigDecimal
import java.time.LocalDate

data class GetItemResponse(

    val i : List<Item>
):Response{
    data class Item(
        val deviceID: Int? = 0,
        val deviceName: String? = null,
        val price: BigDecimal? = null,
        val orderDate: LocalDate? = null,
        val arriveDate: LocalDate?? = null,
        val maintenanceDate: LocalDate?? = null,
        val roomID: Int? = null,
        val state: Char? = null,
        val remark: String? = null
    )
}


