package com.fyp.crms_backend.utils


import com.fyp.crms_backend.utils.JWT
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val jwt: JWT) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoginInterceptor(jwt))
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/loginbypw", "/api/loginbycard", "/api/renewtoken")
    }
}