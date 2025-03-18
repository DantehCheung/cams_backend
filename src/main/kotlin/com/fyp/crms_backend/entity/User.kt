package com.fyp.crms_backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class User(
    @Id
    @Column(name = "CNA")
    var CNA: String = "",

    @Column(name = "emaildomain")
    var emailDomain: String = "",

    @Column(name = "salt")
    var salt: String = "",

    @Column(name = "password")
    var password: String = "",

    @Column(name = "accesslevel")
    var accessLevel: Int = 0,

    @Column(name = "accesspage")
    var accessPage: Int = 0,

    @Column(name = "firstname")
    var firstName: String = "",

    @Column(name = "lastname")
    var lastName: String = "",

    @Column(name = "contentno")
    var contentNo: String = "",

    @Column(name = "campusid")
    var campusID: Int = 0,

    @Column(name = "lastlogintime")
    var lastLoginTime: LocalDateTime? = null,

    @Column(name = "lastloginip")
    var lastLoginIP: String? = null,

    @Column(name = "loginfail")
    var loginFail: Int = 0
)
