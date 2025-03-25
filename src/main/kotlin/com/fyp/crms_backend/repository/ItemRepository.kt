package com.fyp.crms_backend.repository

import com.fyp.crms_backend.dto.item.DeviceWithParts
import com.fyp.crms_backend.entity.CAMSDB
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository



@Repository
class ItemRepository(override val jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

    private val rowMapper = RowMapper<CAMSDB.Device> { rs, _ ->
        CAMSDB.Device(
         deviceID = rs.getInt("deviceID"),
         deviceName = rs.getString("deviceName"),
         price = rs.getBigDecimal("price"),
         orderDate = rs.getDate("orderDate")?.toLocalDate(),
         arriveDate = rs.getDate("arriveDate")?.toLocalDate(),
         maintenanceDate = rs.getDate("maintenanceDate")?.toLocalDate(),
         roomID = rs.getInt("roomID"),
         state = rs.getString("state")?.singleOrNull(),
         remark = rs.getString("remark"),
        )
    }

    fun fetchData(CNA: String,roomID: Int): List<CAMSDB.Device> {

        return super.APIprocess(CNA, "get Device Data") {

            val result: List<CAMSDB.Device> = jdbcTemplate.query(
                """SELECT deviceID,deviceName,price,orderDate,arriveDate,maintenanceDate,device.roomID,state,remark from device,room,user where  device.roomID = room.roomID AND user.CNA = ? AND room.roomID = ?""",
                rowMapper,
                CNA,
                roomID
            )

            return@APIprocess result
        } as List<CAMSDB.Device>

    }

    /*
  fun addItem(CNA:String,roomID: Int, devices: List<DeviceWithParts>) : Boolean {

        return super.APIprocess(CNA,"add Device Data"){
                return@APIprocess
        }
  }*/


}