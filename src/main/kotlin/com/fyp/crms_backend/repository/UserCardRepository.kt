package com.fyp.crms_backend.repository

import com.fyp.crms_backend.algorithm.Snowflake
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserCardRepository(jdbcTemplate: JdbcTemplate, snowflake: Snowflake) :
    ApiRepository(jdbcTemplate, snowflake) {

    fun add(CNA: String, CardID: String, userCNA: String): Boolean {
        return APIprocess(CNA, "Add User Card") {
            // 檢查 CardID 是否存在
            val count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM UserCard WHERE CardID = ?",
                Int::class.java,
                CardID
            ) ?: 0
            if (count > 0) throw RuntimeException("CardID already exists")

            // 插入新紀錄
            jdbcTemplate.update(
                "INSERT INTO UserCard (CardID, CNA) VALUES (?, ?)",
                CardID,
                userCNA
            ) > 0
        } as Boolean
    }

    fun edit(CNA: String, CardID: String, newCardID: String?, state: String?): Boolean {
        return APIprocess(CNA, "Edit User Card") {
            val updates = mutableListOf<String>()
            val params = mutableListOf<Any>()

            newCardID?.let {
                updates.add("CardID = ?")
                params.add(it)
            }
            state?.let {
                updates.add("state = ?")
                params.add(it)
            }

            if (updates.isEmpty()) throw IllegalArgumentException("No fields to update")
            params.add(CardID)

            val query = "UPDATE UserCard SET ${updates.joinToString(", ")} WHERE CardID = ?"
            jdbcTemplate.update(query, *params.toTypedArray()) > 0
        } as Boolean
    }

    fun delete(CNA: String, CardID: String): Boolean {
        return APIprocess(CNA, "Delete User Card") {
            jdbcTemplate.update("UPDATE UserCard SET state = 'D' WHERE CardID = ?", CardID) > 0
        } as Boolean
    }
}