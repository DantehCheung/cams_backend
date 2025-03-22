package com.fyp.crms_backend.controller.user

import com.fyp.crms_backend.dto.login.LoginByPwRequest
import com.fyp.crms_backend.dto.login.LoginByPwResponse
import com.fyp.crms_backend.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LoginController(private val userService: UserService) {

    @PostMapping("/loginbypw")
    fun login(@RequestBody request: LoginByPwRequest, httpRequest: HttpServletRequest): LoginByPwResponse {
        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        return userService.login(request,ipAddress)
    }
}
