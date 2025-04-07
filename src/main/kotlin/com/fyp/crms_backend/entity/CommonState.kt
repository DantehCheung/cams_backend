package com.fyp.crms_backend.entity

enum class CommonState(val code: Char, val description: String) {
    AVAILABLE('A', "Available"),
    DESTROYED('D', "Destroyed");

    companion object {
        fun fromCode(code: Char?): CommonState? =
            code?.let { c -> entries.find { it.code == c } }
    }
}