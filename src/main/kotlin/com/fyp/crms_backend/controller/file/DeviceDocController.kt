package com.fyp.crms_backend.controller.file

import com.fyp.crms_backend.controller.ApiController
import com.fyp.crms_backend.dto.Response
import com.fyp.crms_backend.dto.file.fileListResponse
import com.fyp.crms_backend.dto.tokenRequest
import com.fyp.crms_backend.service.FileStorageService
import com.fyp.crms_backend.utils.JWT
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files/devicedoc")
class DeviceDocController(
    private val storageService: FileStorageService,
    private val jwt: JWT
) : ApiController() {
    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("deviceId") deviceId: Int,
        @RequestHeader("token") token: String
    ): ResponseEntity<Map<String, String>> {


        val fileName = storageService.storeFile(token, deviceId, file)
        return ResponseEntity.ok(
            mapOf(
                "message" to "File uploaded successfully",
                "fileName" to fileName
            )
        )
    }

    @GetMapping("/download/{deviceId}/{fileName:.+}")
    fun downloadFile(
        @PathVariable deviceId: Int,
        @PathVariable fileName: String,
        @RequestHeader("token") token: String
    ): ResponseEntity<Resource> {
        jwt.decrypteToken(token)

        val resource = storageService.loadFile(deviceId, fileName)
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${resource.filename}\""
            )
            .body(resource)
    }


    @PostMapping("/list/{deviceId}")
    fun listFiles(
        @PathVariable deviceId: Int,
        @RequestBody request: tokenRequest
    ): Response {
        return process {
            try {
                // 1. Validate token (throws exception if invalid)
                jwt.decrypteToken(request.token)

                // 2. Try fetching files
                val files = storageService.listDeviceFiles(deviceId)
                return@process fileListResponse(files)

            } catch (e: NoSuchFileException) {
                println(e)
                // Case: No files found → Return empty list (200 OK)
                return@process fileListResponse(emptyList())

            }
        }

    }
}
