package com.fyp.crms_backend.service

import com.fyp.crms_backend.dto.LoginRequest
import com.fyp.crms_backend.dto.LoginResponse
import com.fyp.crms_backend.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.beans.factory.annotation.Value



@Service
class UserService(private val userRepository: UserRepository) {

    @Value("\${security.jwt.secret}")
    lateinit var secretKey:String

    fun login(request: LoginRequest, ipAddress: String): LoginResponse {
        val user = userRepository.findByCNAAndPassword(request.CNA, request.password,ipAddress)
            ?: throw IllegalArgumentException("Invalid CNA or password")

        val token = generateToken(user.CNA, user.accessLevel)
        val refreshToken = generateRefreshToken(user.CNA)

        return LoginResponse(
            token = token,
            refreshToken = refreshToken,
            accessLevel = user.accessLevel,
            accessPage = user.accessPage,
            firstName = user.firstName,
            lastName = user.lastName,
            lastLoginIp = user.lastLoginIP ?: ""
        )
    }

    private fun generateToken(CNA: String, accessLevel: Int): String {
        return Jwts.builder()
            .setSubject(CNA)
            .claim("accessLevel", accessLevel)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, secretKey) //
            .compact()
    }

    private fun generateRefreshToken(CNA: String): String {
        return Jwts.builder()
            .setSubject(CNA)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 604800000)) // 7 days
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}


