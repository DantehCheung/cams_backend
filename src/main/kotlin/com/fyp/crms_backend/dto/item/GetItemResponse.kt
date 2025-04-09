package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Response
import java.math.BigDecimal
import java.time.LocalDate

data class GetItemResponse(

    val device: List<Devices>
) : Response {
    data class Devices(
        val deviceID: Int? = 0,
        val deviceName: String? = null,
        val price: BigDecimal? = null,
        val orderDate: LocalDate? = null,
        val arriveDate: LocalDate?? = null,
        val maintenanceDate: LocalDate?? = null,
        val roomID: Int? = null,
        val state: Char? = null,
        val remark: String? = null,
        val docs: List<DeviceDoc>,
        val partID: List<DevicePartID>,
        val deviceRFID: List<DeviceRFID>,
    )

    data class DeviceDoc(
        val deviceID: Int? = null,
        val docPath: String? = null
    )

    data class DevicePartID(
        val deviceID: Int? = null,
        val devicePartID: Int? = null,
        val devicePartName: String? = null
    )

    data class DeviceRFID(
        val deviceID: Int? = null,
        val devicePartID: Int? = null,
        val RFID: String? = null
    )
}


