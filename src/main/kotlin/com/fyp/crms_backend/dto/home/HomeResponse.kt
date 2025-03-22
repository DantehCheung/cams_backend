package com.fyp.crms_backend.dto.home

import com.fyp.crms_backend.dto.Response
import java.math.BigDecimal

data class HomeResponse(
    val LastLoginTime:String,
    val LastLoginPlace:String
//    ,
//    val PendingConfirmItem:List<PC>
) : Response{
//    data class PC(
//        val deviceCampus: String,
//        val deviceName:String,
//        val devicePartName:String,
//        val  devicePrice: BigDecimal,
//        val  deviceOrderDate: String,
//        val  deviceStoreRoomNumber: String,
//        val  devicePartID: String
//    )
}
