package com.fyp.crms_backend.entity

import java.time.LocalDateTime

class Device (
    var lastLoginTime: LocalDateTime? = null,
    var lastLoginIP: String? = null
    )