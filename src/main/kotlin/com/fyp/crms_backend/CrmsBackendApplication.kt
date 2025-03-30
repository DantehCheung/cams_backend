package com.fyp.crms_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAsync
class CrmsBackendApplication

fun main(args: Array<String>) {

    runApplication<CrmsBackendApplication>(*args)
}
