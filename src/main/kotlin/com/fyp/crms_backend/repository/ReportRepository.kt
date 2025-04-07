package com.fyp.crms_backend.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ReportRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
}