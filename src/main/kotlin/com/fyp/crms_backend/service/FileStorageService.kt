package com.fyp.crms_backend.service

import com.fyp.crms_backend.FileStorageProperties
import com.fyp.crms_backend.exception.FileStorageException
import com.fyp.crms_backend.repository.ItemRepository
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
    private val deviceRepository: ItemRepository
) {
    // 根据设备ID获取存储路径
    private fun getDeviceDir(deviceId: Int): Path {
        return Paths.get(properties.uploadDir)
            .resolve(deviceId.toString())
            .normalize()
            .toAbsolutePath()
    }

    // 处理文件名冲突
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

    // 上传文件
    fun storeFile(deviceId: Int, file: MultipartFile): String {
        // 验证设备存在
        if (!deviceRepository.existsById(deviceId)) {
            throw FileStorageException("Device $deviceId not found")
        }

        // 创建设备目录
        val deviceDir = getDeviceDir(deviceId)
        Files.createDirectories(deviceDir)

        // 处理文件名
        val fileName = resolveFileName(deviceDir, file.originalFilename!!)

        // 保存文件
        try {
            file.inputStream.use { input ->
                Files.copy(input, deviceDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName", ex)
        }

        return fileName
    }

    // 下载文件
    fun loadFile(deviceId: Int, fileName: String): Resource {
        val filePath = getDeviceDir(deviceId).resolve(fileName).normalize()

        if (!Files.exists(filePath)) {
            throw FileNotFoundException("File $fileName not found")
        }

        return UrlResource(filePath.toUri()).also {
            if (!it.isReadable) throw FileNotFoundException("Could not read file: $fileName")
        }
    }

    // 获取设备所有文件
    fun listDeviceFiles(deviceId: Int): List<String> {
        val deviceDir = getDeviceDir(deviceId)
        return Files.list(deviceDir)
            .filter { Files.isRegularFile(it) }
            .map { it.fileName.toString() }
            .toList()
    }
}