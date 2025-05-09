package com.fyp.crms_backend.controller.website

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.home.HomeRequest
import com.fyp.crms_backend.service.HomeService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HomeController(private val homeService: HomeService) : ApiController() {

    @PostMapping("/gethome")
    fun GetHome(@RequestBody request: HomeRequest): Response {
        //return process(request){
        return/*@process*/ homeService.execute(request)
        //}
    }
}
