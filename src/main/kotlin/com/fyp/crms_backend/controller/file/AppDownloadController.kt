package com.fyp.crms_backend.controller.file

import com.fyp.crms_backend.config.FileStorageProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @GetMapping("/download/android")
    fun downloadAndroidFile(
        @RequestParam(name = "auto", defaultValue = "false") autoDownload: Boolean,
        request: HttpServletRequest
    ): ResponseEntity<Any> {

        val fileName = "CAMS_android.apk"
        val file = File("${fileStorageProperties.appdir}${File.separator}$fileName")

        // 检测是否为浏览器请求
        val isBrowser = request.getHeader("User-Agent")?.contains("Mozilla") ?: false

        return if (isBrowser && !autoDownload) {
            // 显示下载消息并触发自动下载
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
            // 返回文件，如果文件不存在则返回 404
            if (!file.exists()) return ResponseEntity.notFound().build()

            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(FileSystemResource(file))
        }
    }

}