package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.Request
import com.fyp.crms_backend.dto.login.*
import com.fyp.crms_backend.dto.stateResponse
import com.fyp.crms_backend.entity.CAMSDB
import com.fyp.crms_backend.repository.UserRepository
import com.fyp.crms_backend.utils.JWT
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service


@Service
class UserService(private val userRepository: UserRepository, jwt: JWT) : ApiService(jwt) {


    fun login(request: Request, ipAddress: String): LoginResponse {
        var user: CAMSDB.User? = null
        if (request is LoginByPwRequest) {
            user = userRepository.findByCNAAndPassword(request.CNA, request.password, ipAddress)
                ?: throw IllegalArgumentException("Invalid CNA or password")
        } else if (request is LoginByCardRequest) {
            user = userRepository.findByCard(request.CardID, ipAddress)
                ?: throw IllegalArgumentException("Invalid Card ID")
        } else {
            throw IllegalArgumentException("Invalid credentials")
        }


        val token = jwt.generateToken(user)
        val refreshToken = jwt.generateRefreshToken(user)

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

    fun renew(request: RenewTokenRequest, ipAddress: String): RenewTokenResponse {

        val data: Claims = decryptToken(request.refreshToken)
        val user: CAMSDB.User = userRepository.renewToken(data.subject, data["salt"].toString(), ipAddress)!!
        val token = jwt.generateToken(user)

        return RenewTokenResponse(token)

    }

    fun changePw(request: ChangePwRequest): stateResponse {
        val data: Claims = decryptToken(request.token)
        val status: Boolean = userRepository.changePw(data.subject, request.oldPassword, request.newPassword)

        return stateResponse(status = status)

    }
}


