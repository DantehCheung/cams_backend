package com.fyp.crms_backend.service

import com.fyp.crms_backend.algorithm.Snowflake
import com.fyp.crms_backend.config.FileStorageProperties
import com.fyp.crms_backend.dto.item.AddItemRequest.DeviceDoc
import com.fyp.crms_backend.exception.FileStorageException
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileStorageService(
    private val properties: FileStorageProperties,
    private val deviceRepository: ItemRepository,
    jdbcTemplate: JdbcTemplate,
    jwt: JWT,
    snowflake: Snowflake
) : ApiService(jwt, jdbcTemplate, snowflake) {
    private fun getDeviceDir(deviceId: Int): Path {
        try {
            return Paths.get(properties.uploadDir)
                .resolve(deviceId.toString())
                .normalize()
                .toAbsolutePath()
        } catch (e: Exception) {
            throw RuntimeException("Path is not exit")
        }

    }

    private fun resolveFileName(deviceDir: Path, fileName: String): String {
        if (!Files.exists(deviceDir.resolve(fileName))) {
            return fileName
        }

        val baseName = fileName.substringBeforeLast(".")
        val extension = fileName.substringAfterLast(".", "")
        val extWithDot = if (extension.isNotEmpty()) ".$extension" else ""

        var counter = 1
        var newName: String
        do {
            newName = "${baseName}_${counter++}$extWithDot"
        } while (Files.exists(deviceDir.resolve(newName)))

        return newName
    }

    fun storeFile(token: String, deviceId: Int, file: MultipartFile): String {
        val data = decryptToken(token)
        if (!deviceRepository.existsById(deviceId)) {
            throw FileStorageException("Device $deviceId not found")
        }

        val deviceDir = getDeviceDir(deviceId)
        Files.createDirectories(deviceDir)

        val fileName = resolveFileName(deviceDir, file.originalFilename!!)

        try {
            file.inputStream.use { input ->
                Files.copy(
                    input, deviceDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            addLog(data.subject, "File $fileName uploaded to device $deviceId")
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName", ex)
        }

        deviceRepository.addDocs(
            deviceId, listOf(DeviceDoc("$deviceId/$fileName"))
        )

        return fileName
    }

    fun loadFile(deviceId: Int, fileName: String): Resource {
        val filePath = getDeviceDir(deviceId).resolve(fileName).normalize()

        if (!filePath.startsWith(getDeviceDir(deviceId))) {
            throw FileNotFoundException("Cannot access file outside device directory")
        }

        if (!Files.exists(filePath) || !deviceRepository.checkDeviceDocAvailable(fileName)) {
            throw FileNotFoundException("File $fileName not found or not available")
        }

        return UrlResource(filePath.toUri()).also {
            if (!it.isReadable) throw FileNotFoundException("Could not read file: $fileName")
        }
    }

    fun listDeviceFiles(deviceId: Int): List<String> {
        val deviceDir = getDeviceDir(deviceId)
        return Files.list(deviceDir)
            .filter { Files.isRegularFile(it) }
            .map { it.fileName.toString() }
            .toList()
    }
}
