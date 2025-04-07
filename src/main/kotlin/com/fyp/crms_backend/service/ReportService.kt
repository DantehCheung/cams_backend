package com.fyp.crms_backend.service

import com.fyp.crms_backend.repository.BorrowRepository
import com.fyp.crms_backend.repository.ReportRepository
import com.fyp.crms_backend.utils.JWT
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class ReportService(private val repository: ReportRepository, jwt: JWT, jdbcTemplate: JdbcTemplate) : ApiService(jwt,jdbcTemplate) {
}