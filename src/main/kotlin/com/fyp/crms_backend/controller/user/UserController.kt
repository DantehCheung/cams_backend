package com.fyp.crms_backend.controller.user

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.login.ChangePwRequest
import com.fyp.crms_backend.dto.login.LoginByCardRequest
import com.fyp.crms_backend.dto.login.LoginByPwRequest
import com.fyp.crms_backend.dto.login.RenewTokenRequest
import com.fyp.crms_backend.dto.user.AddUserRequest
import com.fyp.crms_backend.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserController(private val userService: UserService) : ApiController() {

    @PostMapping("/loginbypw")
    fun loginByPw(
        @RequestBody request: LoginByPwRequest,
        httpRequest: HttpServletRequest
    ): Response {
        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        return process(request) {
            return@process userService.login(request, ipAddress)
        }

    }

    @PostMapping("/loginbycard")
    fun loginByCard(
        @RequestBody request: LoginByCardRequest,
        httpRequest: HttpServletRequest
    ): Response {
        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        return process(request) {
            return@process userService.login(request, ipAddress)
        }
    }

    @PostMapping("/renewtoken")
    fun renewToken(
        @RequestBody request: RenewTokenRequest,
        httpRequest: HttpServletRequest
    ): Response {
        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        return process(request) {
            return@process userService.renew(request, ipAddress)
        }
    }

    @PostMapping("/changepw")
    fun changePw(@RequestBody request: ChangePwRequest): Response {
        return process(request) {
            return@process userService.changePw(request)
        }
    }

    // ADD USER
    @PostMapping("/adduser")
    fun adduser(@RequestBody request: AddUserRequest): Response {
        return process(request) {
            return@process userService.addUser(request)
        }
    }
}
