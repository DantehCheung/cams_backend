package com.fyp.crms_backend.dto.item

import com.fyp.crms_backend.dto.Response

data class ManualInventoryResponse(
    val manualInventoryLists: List<InventoryItem>
) : Response {
    data class InventoryItem(
        val deviceName: String,
        val devicePartName: String,
        val RFID: String,
        val preState: Char,
        val afterState: Char
    )
}
