package com.fyp.crms_backend.utils

import com.fyp.crms_backend.entity.CAMSDB
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWT(private val sKey: SKey) {

    fun decrypteToken(token: String): Claims {
        return try {
            // 解密你條貞操褲
            val key = sKey.secretKey

            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

            claims
        } catch (e: Exception) {
            println("Token驗證失敗: ${e.message}")
            throw IllegalArgumentException("無效的Token")
        }
    }

    fun generateToken(user: CAMSDB.User): String {
        return Jwts.builder()
            .setSubject(user.CNA)
            .claim("accessLevel", user.accessLevel)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3000000)) // 50 mins
            .signWith(SignatureAlgorithm.HS256, sKey.secretKey) //
            .compact()
    }

    fun generateRefreshToken(user: CAMSDB.User): String {
        return Jwts.builder()
            .setSubject(user.CNA)
            .claim("salt", user.salt)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 604800000)) // 7 days
            .signWith(SignatureAlgorithm.HS256, sKey.secretKey)
            .compact()
    }

}