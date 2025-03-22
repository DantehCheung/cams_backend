package com.fyp.crms_backend.repository

import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository



@Repository
class ItemRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {
    private val rowMapper = RowMapper<CAMSDB.Device> { rs, _ ->
        CAMSDB.Device(

         deviceID = rs.getInt("deviceID"),
         deviceName = rs.getString("deviceName"),
         price = rs.getBigDecimal("price"),
         orderDate = rs.getDate("orderDate")?.toLocalDate(),
         arriveDate = rs.getDate("arriveDate")?.toLocalDate(),
         maintenanceDate = rs.getDate("maintenanceDate")?.toLocalDate(),
         roomID = rs.getInt("roomID "),
         state = rs.getString("state")?.singleOrNull(),
         remark = rs.getString("remark"),
        )
    }

    fun fetchData(CNA: String): CAMSDB.Device {

        return super.APIprocess(CNA, "get Device Data") {
            val result: List<CAMSDB.Device> = jdbcTemplate.query(
                """select deviceID,deviceName,price,orderDate,arriveDate,maintenanceDate,device.roomID,state,remark from device, room where device.roomID = room.roomID and CNA = ? """,
                rowMapper,
                CNA
            )

            return@APIprocess result.firstOrNull()
        } as CAMSDB.Device

    }


}