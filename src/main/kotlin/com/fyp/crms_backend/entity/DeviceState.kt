package com.fyp.crms_backend.entity

enum class DeviceState(val code: Char, val description: String) {
    SHIPPING('S', "Shipping"),
    AVAILABLE('A', "Available"),
    RESERVE('R', "Reserved"),
    ON_LOAN('L', "On Loan"),
    EXPIRED('E', "Expired"),
    BROKEN('B', "Broken"),
    WAITING_REPAIR('W', "Waiting Repair"),
    DESTROYED('D', "Destroyed"),
    MISSING('M', "Missing");

    companion object {
        fun fromCode(code: Char?): DeviceState? =
            code?.let { c -> entries.find { it.code == c } }
    }
}