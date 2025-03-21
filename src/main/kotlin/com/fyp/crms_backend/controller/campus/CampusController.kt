package com.fyp.crms_backend.controller.campus

import com.fyp.crms_backend.dto.campus.CampusRequest
import com.fyp.crms_backend.dto.campus.CampusResponse
import com.fyp.crms_backend.service.CampusService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CampusController(private val campusService: CampusService) {

    @PostMapping("/campus")
    fun GetCampus(
        @RequestBody request: CampusRequest,
        httpRequest: HttpServletRequest
    ): CampusResponse {
        return campusService.execute(request)
    }
}