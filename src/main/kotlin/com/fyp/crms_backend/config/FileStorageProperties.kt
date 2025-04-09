package com.fyp.crms_backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file")
class FileStorageProperties {
    lateinit var uploadDir: String
    lateinit var appdir: String
}