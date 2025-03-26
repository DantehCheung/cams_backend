package com.fyp.crms_backend.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class BorrowRepository(jdbcTemplate: JdbcTemplate) : ApiRepository(jdbcTemplate) {

    private fun checkBookingAvailable(itemID: Int, startDate: LocalDate): Boolean {
        return true
    }

    private fun checkReturnAvailable(itemID:Int):Boolean
    {
        return true
    }

    @Transactional
    fun reservation(CNA: String, itemID: Int, borrowDate: LocalDate): Boolean {
        return super.APIprocess(CNA, "reservation") {
            if (!checkBookingAvailable(itemID, borrowDate)) {
                return@APIprocess false
            }

            val result: Int = jdbcTemplate.update(
                """INSERT INTO `cams`.`deviceborrowrecord`
(
`borrowDate`,
`deviceID`,
`borrowUserCNA`,
`leasePeriod`)
VALUES
?,
?,
?,
?)""",
                borrowDate, itemID, CNA, borrowDate.plusDays(14)
            )
            jdbcTemplate.update(
                """INSERT INTO `cams`.`deviceborrowrecord`
(
`borrowDate`,
`deviceID`,
`borrowUserCNA`,
`leasePeriod`)
VALUES
?,
?,
?,
?)""",
                borrowDate, itemID, CNA, borrowDate.plusDays(14)
            )

            return@APIprocess result > 0
        } as Boolean

    }

    fun borrow(CNA: String, itemID: Int): Boolean {
        return super.APIprocess(CNA, "borrow") {
            if (!checkBookingAvailable(itemID, LocalDate.now())) {
                return@APIprocess false
            }
            val result: Int = jdbcTemplate.update(
                """INSERT INTO `deviceborrowrecord` (`deviceID`, `borrowUserCNA`) VALUES ( ?, ?)""",
                itemID, CNA
            )

            return@APIprocess result > 0
        } as Boolean
    }

    fun remand(CNA: String, returnList: List<Int>): List<Boolean> {
        val stateList: List<Boolean> = List<Boolean>(returnList.size) { false }
        return super.APIprocess(CNA, "remand") {
            returnList.map { itemID ->
                if (!checkReturnAvailable(itemID)) {
                    return@APIprocess false
                }
                val result: Int = jdbcTemplate.update(
                    """INSERT INTO `deviceborrowrecord` (`deviceID`, `borrowUserCNA`) VALUES ( ?, ?)""",
                    itemID, CNA
                )
            }
            return@APIprocess stateList
        } as List<Boolean>
        """INSERT INTO `cams`.`checkdevicereturnrecord`
(`checkRecordID`,
`checkDT`,
`inspector`)
VALUES
(<{checkRecordID: }>,
<{checkDT: }>,
<{inspector: }>);
"""
        """INSERT INTO `cams`.`checkdevicereturnrecord`
(`checkRecordID`, `checkDT`, `inspector`)
VALUES
(1, '2025-03-26 12:00:00', 'John Doe'),
(2, '2025-03-26 12:30:00', 'Jane Smith'),
(3, '2025-03-26 13:00:00', 'Mike Johnson');"""
        throw Exception()
    }

    fun getBorrowList(
        CNA: String? = null,
        borrowDateAfter: LocalDate = LocalDate.of(2000, 1, 1),
        returned: Boolean = false
    ): List<Int> {
        throw Exception("")

    }

    fun checkRemand(CNA: String) {
        throw Exception("")
    }


}