package com.fyp.crms_backend.service

import com.fyp.crms_backend.FileStorageProperties
import com.fyp.crms_backend.exception.FileStorageException
import com.fyp.crms_backend.repository.ItemRepository
import com.fyp.crms_backend.utils.JWT
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
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
    jwt: JWT
) : ApiService(jwt) {
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

    //TODO: insert to db
    fun storeFile(deviceId: Int, file: MultipartFile): String {
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
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName", ex)
        }

        return fileName
    }

    // TODO: check state
    fun loadFile(deviceId: Int, fileName: String): Resource {
        val filePath = getDeviceDir(deviceId).resolve(fileName).normalize()

        if (!Files.exists(filePath)) {
            throw FileNotFoundException("File $fileName not found")
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
