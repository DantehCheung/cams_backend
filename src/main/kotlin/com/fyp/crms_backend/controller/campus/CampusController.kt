package com.fyp.crms_backend.controller.campus


import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.campus.AddCampusRequest
import com.fyp.crms_backend.dto.campus.DeleteCampusRequest
import com.fyp.crms_backend.dto.campus.EditCampusRequest
import com.fyp.crms_backend.dto.campus.GetCampusRequest
import com.fyp.crms_backend.service.CampusService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CampusController(private val campusService: CampusService) : ApiController() {

    @PostMapping("/getcampus")
    fun getCampus(
        @RequestBody request: GetCampusRequest
    ): Response {
        return process(request) {
            return@process campusService.get(request)
        }
    }

    @PostMapping("/addcampus")
    fun AddCampus(
        @RequestBody request: AddCampusRequest
    ): Response {
        return process(request) {
            return@process campusService.add(request)
        }
    }

    @PostMapping("/editcampus")
    fun EditCampus(
        @RequestBody request : EditCampusRequest
    ): Response {
        return process(request) {
            return@process campusService.edit(request)
        }
    }

    @PostMapping("/deletecampus")
    fun DeleteCampuus(
        @RequestBody request: DeleteCampusRequest
    ) : Response{
        return process(request) {
            return@process campusService.delete(request)
        }
    }


}