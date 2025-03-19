package com.fyp.crms_backend.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class CAMSDB(
    val users: List<User>,
    val userCards: List<UserCard>,
    val campuses: List<Campus>,
    val rooms: List<Room>,
    val roomRFIDs: List<RoomRFID>,
    val devices: List<Device>,
    val deviceDocs: List<DeviceDoc>,
    val devicePartIDs: List<DevicePartID>,
    val deviceRFIDs: List<DeviceRFID>,
    val deviceBorrowRecords: List<DeviceBorrowRecord>,
    val deviceReturnRecords: List<DeviceReturnRecord>,
    val checkDeviceReturnRecords: List<CheckDeviceReturnRecord>,
    val logs: List<Log>
) {
    data class User(
        val CNA: String,
        val emailDomain: String,
        val salt: String,
        val password: String,
        val accessLevel: Int = 10000,
        val accessPage: Int = 0,
        val firstName: String = "",
        val lastName: String = "",
        val contentNo: String,
        val campusID: Int,
        val lastLoginTime: LocalDateTime? = LocalDateTime.now(),
        val lastLoginIP: String? = null,
        val loginFail: Int = 0
    )

    data class UserCard(
        val cardID: String,
        val CNA: String
    )

    data class Campus(
        val campusID: Int = 0,
        val campusShortName: String,
        val campusName: String
    )

    data class Room(
        val roomID: Int = 0,
        val campusID: Int,
        val roomNumber: String,
        val roomName: String = ""
    )

    data class RoomRFID(
        val roomID: Int,
        val RFID: String
    )

    data class Device(
        val deviceID: Int = 0,
        val deviceName: String,
        val price: BigDecimal,
        val orderDate: LocalDate,
        val arriveDate: LocalDate? = null,
        val maintenanceDate: LocalDate? = null,
        val roomID: Int,
        val state: Char,
        val remark: String = ""
    )

    data class DeviceDoc(
        val deviceID: Int,
        val docPath: String
    )

    data class DevicePartID(
        val deviceID: Int,
        val devicePartID: Int,
        val devicePartName: String
    )

    data class DeviceRFID(
        val deviceID: Int,
        val devicePartID: Int,
        val RFID: String
    )

    data class DeviceBorrowRecord(
        val borrowRecordID: Int = 0,
        val borrowDate: LocalDate = LocalDate.now(),
        val deviceID: Int,
        val borrowUserCNA: String,
        val leasePeriod: LocalDate = LocalDate.now().plusDays(14)
    )

    data class DeviceReturnRecord(
        val borrowRecordID: Int,
        val returnDate: LocalDate = LocalDate.now(),
        val checkRecordID: Int? = null
    )

    data class CheckDeviceReturnRecord(
        val checkRecordID: Int = 0,
        val checkDT: LocalDateTime,
        val inspector: String
    )

    data class Log(
        val DT: LocalDateTime = LocalDateTime.now(),
        val userCNA: String,
        val log: String
    )
}

