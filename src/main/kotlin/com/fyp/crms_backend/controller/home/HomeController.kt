package com.fyp.crms_backend.controller.home

import org.springframework.web.bind.annotation.*
import com.fyp.crms_backend.dto.HomeRequest
import com.fyp.crms_backend.dto.HomeResponse
import com.fyp.crms_backend.service.UserService
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class HomeController(private val userService: UserService){

    @PostMapping("/home")
    fun GetHome(@RequestBody request: HomeRequest,httpRequest: HttpServletRequest): HomeResponse {

        val ipAddress = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr
        println(ipAddress)
        return userService.login(request,ipAddress)
    }
}
