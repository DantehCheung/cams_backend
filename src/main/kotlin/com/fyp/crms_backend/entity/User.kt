package com.fyp.crms_backend.entity

import java.time.LocalDateTime

data class User(
    var CNA: String = "",
    var emailDomain: String = "",
    var salt: String = "",
    var password: String = "",
    var accessLevel: Int = 0,
    var accessPage: Int = 0,
    var firstName: String = "",
    var lastName: String = "",
    var contentNo: String = "",
    var campusID: Int = 0,
    var campusShortName: String = "",
    var campusName: String = "",
    var CardID: String = "",
    var lastLoginTime: LocalDateTime? = null,
    var lastLoginIP: String? = null,
    var loginFail: Int = 0,
    var UserLog: String = "",
)
