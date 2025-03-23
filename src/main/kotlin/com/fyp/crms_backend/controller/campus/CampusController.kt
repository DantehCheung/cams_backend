package com.fyp.crms_backend.controller.campus


import com.fyp.crms_backend.dto.campus.*
import com.fyp.crms_backend.service.CampusService
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
        return campusService.get(request)
    }

    @PostMapping("/addcampus")
    fun AddCampus(
        @RequestBody request: AddCampusRequest
    ) : AddCampusResponse {
        return campusService.add(request)
    }

    @PostMapping("/editcampus")
    fun EditCampus(
        @RequestBody request : EditCampusRequest
    ) : EditCampusResponse {
        return campusService.edit(request)
    }


}