package com.fyp.crms_backend.controller.user

import org.springframework.web.bind.annotation.*
import com.fyp.crms_backend.dto.LoginRequest
import com.fyp.crms_backend.dto.LoginResponse
import com.fyp.crms_backend.service.UserService
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class LoginController(private val userService: UserService) {

    @PostMapping("/loginbypw")
    fun login(@RequestBody request: LoginRequest,httpRequest: HttpServletRequest): LoginResponse {
        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        println(ipAddress)
        return userService.login(request,ipAddress)
    }
}
