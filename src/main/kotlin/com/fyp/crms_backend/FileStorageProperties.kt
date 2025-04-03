package com.fyp.crms_backend

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file")
class FileStorageProperties {
    lateinit var uploadDir: String
}