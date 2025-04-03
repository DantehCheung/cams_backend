package com.fyp.crms_backend.controller

import com.fyp.crms_backend.service.FileStorageService
import com.fyp.crms_backend.utils.JWT
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileController(
    private val storageService: FileStorageService,
    private val jwt: JWT
) {
    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("deviceId") deviceId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Map<String, String>> {
        jwt.decrypteToken(token)

        val fileName = storageService.storeFile(deviceId, file)
        return ResponseEntity.ok(mapOf(
            "message" to "File uploaded successfully",
            "fileName" to fileName
        ))
    }

    @GetMapping("/download/{deviceId}/{fileName:.+}")
    fun downloadFile(
        @PathVariable deviceId: Int,
        @PathVariable fileName: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Resource> {
        jwt.decrypteToken(token)

        val resource = storageService.loadFile(deviceId, fileName)
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${resource.filename}\"")
            .body(resource)
    }

    @GetMapping("/list/{deviceId}")
    fun listFiles(
        @PathVariable deviceId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<String>> {
        jwt.decrypteToken(token)

        val files = storageService.listDeviceFiles(deviceId)
        return ResponseEntity.ok(files)
    }
}
