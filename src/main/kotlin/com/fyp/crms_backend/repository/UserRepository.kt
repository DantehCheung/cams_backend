package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface UserRepository : JpaRepository<User, String> {
    @Query(
        value = """
            SELECT * 
            FROM user 
            WHERE CNA = :CNA 
              AND password = '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c'
        """, nativeQuery = true
    )
    fun findByCNAAndPassword(@Param("CNA") CNA: String, @Param("password") password: String): User?
}
