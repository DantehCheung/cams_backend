package com.fyp.crms_backend.controller.userCard

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.userCard.AddUserCardRequest
import com.fyp.crms_backend.dto.userCard.DeleteUserCardRequest
import com.fyp.crms_backend.dto.userCard.EditUserCardRequest
import com.fyp.crms_backend.service.UserCardService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserCardController(private val userCardService: UserCardService) : ApiController() {

    @PostMapping("/addusercard")
    fun addUserCard(@RequestBody request: AddUserCardRequest): Response {
        return process(request) { userCardService.add(request) }
    }

    @PostMapping("/editusercard")
    fun editUserCard(@RequestBody request: EditUserCardRequest): Response {
        return process(request) { userCardService.edit(request) }
    }

    @PostMapping("/deleteusercard")
    fun deleteUserCard(@RequestBody request: DeleteUserCardRequest): Response {
        return process(request) { userCardService.delete(request) }
    }
}