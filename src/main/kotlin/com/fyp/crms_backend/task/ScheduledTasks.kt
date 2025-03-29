package com.fyp.crms_backend.task

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ScheduledTasks(
    private val jdbcTemplate: JdbcTemplate,
    private val mailSender: JavaMailSender
) {
    data class OverdueRecord(
        val CNA: String,
        val email: String,
        val deviceName: String,
        val leasePeriod: LocalDate
    )
    // 每天上午9点执行逾期检查
    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Hong_Kong")
    @Transactional
    fun checkOverdueDevices() {
        val overdueRecords = jdbcTemplate.query("""
            SELECT br.borrowRecordID, u.CNA, d.deviceName, br.leasePeriod, 
                   CONCAT(u.CNA, '@', u.emailDomain) AS email
            FROM DeviceBorrowRecord br
            JOIN User u ON br.borrowUserCNA = u.CNA
            JOIN Device d ON br.deviceID = d.deviceID
            LEFT JOIN DeviceReturnRecord rr ON br.borrowRecordID = rr.borrowRecordID
            WHERE u.accessLevel = 1000
              AND d.state = 'L' 
              AND rr.returnDate IS NULL
              AND br.leasePeriod < CURDATE()
        """) { rs, _ ->
            OverdueRecord(
                rs.getString("CNA"),
                rs.getString("email"),
                rs.getString("deviceName"),
                rs.getDate("leasePeriod").toLocalDate()
            )
        }

        overdueRecords.forEach { record ->
            sendReminderEmail(record)
        }
    }

    // 每天午夜检查当日预订
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Hong_Kong")
    @Transactional
    fun updateReservationStatus() {
        jdbcTemplate.update("""
            UPDATE Device d
            JOIN DeviceBorrowRecord br ON d.deviceID = br.deviceID
            SET d.state = 'R'
            WHERE d.state = 'A'
              AND br.borrowDate = CURDATE()
              AND NOT EXISTS (
                SELECT 1 FROM DeviceReturnRecord 
                WHERE borrowRecordID = br.borrowRecordID
              )
        """)
    }

    @Scheduled(cron = "6 45 23 * * *", zone = "Asia/Hong_Kong")
    fun sendDailyEmail() {
        val message = SimpleMailMessage().apply {
            setTo("abc@gmail.com")
            setSubject("test")
            text = "这是每天 23:45:06 自动发送的测试邮件"
        }

        mailSender.send(message)
        println("邮件已发送于 ${java.time.LocalDateTime.now()}")
    }

    private fun sendReminderEmail(record: OverdueRecord) {
        SimpleMailMessage().apply {
            setTo(record.email)
            subject = "asshole"
            text = """
                ass${record.CNA}ass
            """.trimIndent()
        }.also {
            mailSender.send(it)
        }
    }


}