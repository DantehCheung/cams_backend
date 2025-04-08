package com.fyp.crms_backend.utils

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    @Configuration
    class WebConfig : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:5174",
                    "http://localhost:5175",
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:5174",
                    "http://127.0.0.1:5175",
                    "http://172.18.37.6:5173",
                    "http://172.18.37.6:5174",
                    "http://172.18.37.6:5175",
                    "http://192.168.237.1:5173",
                    "http://192.168.237.1:5174",
                    "http://192.168.237.1:5175",
                    "http://192.168.192.1:5173",
                    "http://192.168.192.1:5174",
                    "http://192.168.192.1:5175",
                    "http://192.168.30.10:5173",
                    "http://192.168.30.10:5174",
                    "http://192.168.30.10:5175"
                )
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true)
        }
    }
}