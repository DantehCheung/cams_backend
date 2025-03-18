package com.fyp.crms_backend.service

import com.fyp.crms_backend.LoginRequest
import com.fyp.crms_backend.dto.LoginResponse
import com.fyp.crms_backend.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    private val secretKey = "yourSecretKey"

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByCNAAndPassword(request.CNA, request.password)
            ?: throw IllegalArgumentException("Invalid CNA or password")

        // Generate tokens
        val token = generateToken(user.CNA, user.accessLevel)
        val refreshToken = generateRefreshToken(user.CNA)

        // Update user last login details (optional logic)
        // updateLastLogin(user.CNA, request.ip)

        return LoginResponse(
            token = token,
            refreshToken = refreshToken,
            accessLevel = user.accessLevel,
            accessPage = user.accessPage,
            firstName = user.firstName,
            lastName = user.lastName
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
