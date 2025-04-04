package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.home.HomeResponse
import com.fyp.crms_backend.dto.home.PC
import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.time.LocalDate


@Repository
class HomeRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.User> { rs, _ ->
        CAMSDB.User(
            lastLoginTime = rs.getTimestamp("lastLoginTime")?.toLocalDateTime(),
            lastLoginIP = rs.getString("lastLoginIP"),
        )
    }

    private val rowMapper1 = RowMapper<PC> { rs, _ ->
        PC(
            deviceID = rs.getInt("deviceID"),
            deviceName = rs.getString("deviceName"),
             price = rs.getBigDecimal("price"),
        orderDate = rs.getDate("orderDate")?.toLocalDate(),
        roomID = rs.getInt("roomID"),
        state = rs.getString("state").toCharArray()[0],
        remark = rs.getString("remark"),
        )
    }

    fun fetchData(CNA:String): HomeResponse {


            val result: CAMSDB.User = jdbcTemplate.queryForObject(
                """
                SELECT lastLoginTime, lastLoginIP
                FROM user
                WHERE CNA = ?
                """.trimIndent(),
                rowMapper,
                CNA
            ) ?: throw IllegalArgumentException("null")

            val result1: List<PC> = jdbcTemplate.query(
                """
                SELECT deviceID, deviceName, price, orderDate, roomID, state, remark
                FROM device
                WHERE state = 'S'
                """.trimIndent(),
                rowMapper1
            )

            return HomeResponse(
                LastLoginTime = result.lastLoginTime!!.toString(),
                LastLoginPlace = result.lastLoginIP!!,
                PendingConfirmItem = result1
            )
        }










}