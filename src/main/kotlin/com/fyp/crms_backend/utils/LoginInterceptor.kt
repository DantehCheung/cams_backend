package com.fyp.crms_backend.utils

import com.fyp.crms_backend.utils.JWT
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class LoginInterceptor(private val jwt: JWT) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.substringAfter("Bearer ") ?: ""
        if (token.isEmpty()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Token missing")
            return false
        }
        try {
            jwt.decrypteToken(token)
        } catch (ex: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Invalid token")
            return false
        }
        return true
    }
}