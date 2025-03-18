package com.fyp.crms_backend.controller

import org.springframework.web.bind.annotation.*
import com.fyp.crms_backend.LoginRequest
import com.fyp.crms_backend.dto.LoginResponse
import com.fyp.crms_backend.service.UserService

@RestController
@RequestMapping("/api")
class LoginController(private val userService: UserService) {

    @PostMapping("/loginbypw")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return userService.login(request)
    }
}
