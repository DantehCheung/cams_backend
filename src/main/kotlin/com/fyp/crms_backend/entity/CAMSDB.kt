package com.fyp.crms_backend.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class CAMSDB(
    val users: List<User>? = null,
    val userCards: List<UserCard>? = null,
    val campuses: List<Campus>? = null,
    val rooms: List<Room>? = null,
    val roomRFIDs: List<RoomRFID>? = null,
    val devices: List<Device>? = null,
    val deviceDocs: List<DeviceDoc>? = null,
    val deviceParts: List<DevicePart>? = null,
    val deviceRFIDs: List<DeviceRFID>? = null,
    val deviceBorrowRecords: List<DeviceBorrowRecord>? = null,
    val deviceReturnRecords: List<DeviceReturnRecord>? = null,
    val checkDeviceReturnRecords: List<CheckDeviceReturnRecord>? = null,
    val logs: List<Log>? = null
) {
    data class User(
        val CNA: String? = null,
        val emailDomain: String? = null,
        val salt: String? = null,
        val password: String? = null,
        val accessLevel: Int = 10000,
        val accessPage: Int = 0,
        val firstName: String = "",
        val lastName: String = "",
        val contentNo: String? = null,
        val campusID: Int? = null,
        val lastLoginTime: LocalDateTime? = null,
        val lastLoginIP: String? = null,
        val loginFail: Int = 0
    )

    data class UserCard(
        val CardID: String? = null,
        val CNA: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class Campus(
        val campusID: Int? = null,
        val campusShortName: String? = null,
        val campusName: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class Room(
        val roomID: Int? = null,
        val campusID: Int? = null,
        val roomNumber: String? = null,
        val roomName: String = "",
        val state: CommonState = CommonState.AVAILABLE
    )

    data class RoomRFID(
        val roomID: Int? = null,
        val RFID: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class Device(
        val deviceID: Int? = null,
        val deviceName: String? = null,
        val price: BigDecimal? = null,
        val orderDate: LocalDate? = null,
        val arriveDate: LocalDate? = null,
        val maintenanceDate: LocalDate? = null,
        val roomID: Int? = null,
        val state: DeviceState = DeviceState.AVAILABLE,
        val remark: String = ""
    )

    data class DeviceDoc(
        val deviceID: Int? = null,
        val docPath: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class DevicePart(
        val deviceID: Int? = null,
        val devicePartID: Int? = null,
        val devicePartName: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class DeviceRFID(
        val deviceID: Int? = null,
        val devicePartID: Int? = null,
        val RFID: String? = null,
        val state: CommonState = CommonState.AVAILABLE
    )

    data class DeviceBorrowRecord(
        val borrowRecordID: Int? = null,
        val borrowDate: LocalDate = LocalDate.now(),
        val deviceID: Int? = null,
        val borrowUserCNA: String? = null,
        val leasePeriod: LocalDate = LocalDate.now().plusDays(14)
    )

    data class DeviceReturnRecord(
        val borrowRecordID: Int? = null,
        val returnDate: LocalDate = LocalDate.now(),
        val checkRecordID: Int? = null
    )

    data class CheckDeviceReturnRecord(
        val checkRecordID: Int? = null,
        val checkDT: LocalDateTime = LocalDateTime.now(),
        val inspector: String? = null
    )

    data class Log(
        val ID: Long? = null,
        val DT: LocalDateTime = LocalDateTime.now(),
        val userCNA: String? = null,
        val log: String? = null
    )
}