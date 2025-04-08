package com.fyp.crms_backend.controller.file

import com.fyp.crms_backend.FileStorageProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File


@RestController
@RequestMapping("/api/files/devicedoc")
class AppDownloadController(
    private val fileStorageProperties: FileStorageProperties // Inject config properties
) {
    @GetMapping("/download/{OS}/{type}")
    fun downloadFile(
        @PathVariable OS: String,
        @PathVariable type: String,
        //@RequestHeader("token") token: String
    ): ResponseEntity<Resource> {
        // 1. Generate dynamic filename
        val fileName = "${OS}_${type}.zip"

        // 2. Build full path using configured app directory
        val file = File("${fileStorageProperties.appdir}${File.separator}$fileName")

        // 3. Check file existence
        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        // 4. Prepare resource and headers
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(FileSystemResource(file))
    }
}