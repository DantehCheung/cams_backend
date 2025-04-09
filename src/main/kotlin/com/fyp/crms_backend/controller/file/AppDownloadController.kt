package com.fyp.crms_backend.controller.file

import com.fyp.crms_backend.config.FileStorageProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File


@RestController
@RequestMapping("/api/files/app")
class AppDownloadController(
    private val fileStorageProperties: FileStorageProperties
) {
    @GetMapping("/download/{OS}/{type}")
    fun downloadFile(
        @PathVariable OS: String,
        @PathVariable type: String,
        @RequestParam(name = "auto", defaultValue = "false") autoDownload: Boolean,
        request: HttpServletRequest
    ): ResponseEntity<Any> {

        val fileName = "CAMS_${OS}_${type}.zip"
        val file = File("${fileStorageProperties.appdir}${File.separator}$fileName")

        // Detect browser requests
        val isBrowser = request.getHeader("User-Agent")?.contains("Mozilla") ?: false

        return if (isBrowser && !autoDownload) {
            // Show download message and trigger auto-download
            ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(
                    """
                    <html>
                        <body>
                            <script>
                                setTimeout(function() {
                                    window.location.href = window.location.href + "?auto=true";
                                }, 300);
                            </script>
                            <h2>The file is downloading...</h2>
                        </body>
                    </html>
                """.trimIndent()
                )
        } else {
            // Return file if exists
            if (!file.exists()) return ResponseEntity.notFound().build()

            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(FileSystemResource(file))
        }
    }
}