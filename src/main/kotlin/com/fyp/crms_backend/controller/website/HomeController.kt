package com.fyp.crms_backend.controller.website

import com.fyp.crms_backend.dto.home.HomeRequest
import com.fyp.crms_backend.dto.home.HomeResponse
import com.fyp.crms_backend.service.HomeService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HomeController(private val homeService: HomeService) {

    @PostMapping("/gethome")
    fun GetHome(@RequestBody request: HomeRequest): HomeResponse {
        return homeService.execute(request)
    }
}
