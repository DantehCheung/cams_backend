package com.fyp.crms_backend.dto.roomRfid

import com.fyp.crms_backend.dto.Request

data class AddRoomRFIDRequest(
    val roomID: Int,
    val RFID: String,
    val token: String
) : Request

data class EditRoomRFIDRequest(
    val RFID: String,
    val newRFID: String? = null,
    val state: String? = null,
    val token: String
) : Request

data class DeleteRoomRFIDRequest(
    val RFID: String,
    val token: String
) : Request

data class GetRoomByRFIDRequest(
    val token: String,
    val RFID: String,

) : Request