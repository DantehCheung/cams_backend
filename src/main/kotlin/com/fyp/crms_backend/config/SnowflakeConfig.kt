package com.fyp.crms_backend.config

import com.fyp.crms_backend.algorithm.Snowflake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SnowflakeConfig {

    private val workerId0: Int = 0
    private val workerId1: Int = 1
    private val workerId2: Int = 2
    private val workerId3: Int = 3


    @Bean(name = ["snowflakeDatacenter0"])
    @Primary
    fun snowflakeDatacenter0(): Snowflake {
        return Snowflake(1, workerId0.toLong())
    }

    @Bean(name = ["snowflakeDatacenter1"])
    fun snowflakeDatacenter1(): Snowflake {
        return Snowflake(1, workerId1.toLong())
    }

    @Bean(name = ["snowflakeDatacenter2"])
    fun snowflakeDatacenter2(): Snowflake {
        return Snowflake(1, workerId2.toLong())
    }

    @Bean(name = ["snowflakeDatacenter3"])
    fun snowflakeDatacenter3(): Snowflake {
        return Snowflake(1, workerId3.toLong())
    }


}