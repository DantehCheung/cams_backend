package com.fyp.crms_backend.controller.campus

import com.fyp.crms_backend.dto.campus.GetCampusRequest
import com.fyp.crms_backend.dto.campus.GetCampusResponse
import com.fyp.crms_backend.service.CampusService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CampusController(private val campusService: CampusService) {

    @PostMapping("/getcampus")
    fun getCampus(
        @RequestBody request: GetCampusRequest
    ): GetCampusResponse {
        return campusService.execute(request)
    }
}