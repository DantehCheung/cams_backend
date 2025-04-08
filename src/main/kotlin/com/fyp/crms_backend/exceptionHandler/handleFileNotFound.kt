package com.fyp.crms_backend.exceptionHandler

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

class FileNotFoundException : RuntimeException("File not found")

@ExceptionHandler(FileNotFoundException::class)
@ResponseStatus(HttpStatus.NOT_FOUND)
fun handleFileNotFound(ex: FileNotFoundException): Map<String, String> {
    return mapOf("error" to ex.message!!)
}