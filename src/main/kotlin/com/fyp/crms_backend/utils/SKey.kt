package com.fyp.crms_backend.utils

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SKey {
    @Value("\${security.jwt.secret}")
    lateinit var secretKey: String

    @PostConstruct
    fun init() {
        println("Secret Key Initialized")
    }
}