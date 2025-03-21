package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository


@Repository
class CampusRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.Campus> { rs, _ ->
        CAMSDB.Campus(
            campusID = rs.getInt("campusID"),
            campusShortName = rs.getString("campusShortName"),
            campusName = rs.getString("campusName"),
        )
    }

    fun fetchData(CNA: String): CAMSDB.Campus {

        return super.APIprocess(CNA, "get Campus Data") {
            val result: List<CAMSDB.Campus> = jdbcTemplate.query(
                """select campus.campusID,campusShortName,campusName from User, campus where User.campusID = campus.campusID and CNA = ? """,
                rowMapper,
                CNA
            )

            return@APIprocess result.firstOrNull()
        } as CAMSDB.Campus

    }


}