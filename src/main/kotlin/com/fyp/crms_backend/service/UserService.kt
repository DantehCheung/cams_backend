package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.LoginRequest
import com.fyp.crms_backend.dto.LoginResponse
import com.fyp.crms_backend.repository.UserRepository
import com.fyp.crms_backend.utils.JWT
import org.springframework.stereotype.Service


@Service
class UserService(private val userRepository: UserRepository, private val jwt: JWT) {


    fun login(request: LoginRequest, ipAddress: String): LoginResponse {
        val user = userRepository.findByCNAAndPassword(request.CNA, request.password,ipAddress)
            ?: throw IllegalArgumentException("Invalid CNA or password")


        val token = jwt.generateToken(user.CNA!!, user.accessLevel!!)
        val refreshToken = jwt.generateRefreshToken(user.CNA!!)

        return LoginResponse(
            token = token,
            refreshToken = refreshToken,
            accessLevel = user.accessLevel!!,
            accessPage = user.accessPage!!,
            firstName = user.firstName!!,
            lastName = user.lastName!!,
            lastLoginIp = user.lastLoginIP ?: ""
        )
    }

}


