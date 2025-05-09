plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.fyp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    dependencies {
       // implementation("org.projectlombok:lombok")
        implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-mail") {
            exclude(group = "com.sun.mail", module = "javax.mail") // Exclude legacy JavaMail
        }
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.jsonwebtoken:jjwt-api:0.11.5")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
        implementation("mysql:mysql-connector-java:8.0.28")
        implementation("com.sun.mail:jakarta.mail:2.0.1")
       // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
      //  implementation("org.hibernate.orm:hibernate-core:6.3.1.Final") STOPs

    }

}


kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.fyp.crms_backend.CrmsBackendApplicationKt" // Replace with your main class
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // Add this line
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}