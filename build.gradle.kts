plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "corp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.netty:netty-resolver-dns-native-macos")

    //MySql
    implementation("io.asyncer:r2dbc-mysql:1.0.4")
    runtimeOnly("com.mysql:mysql-connector-j")

    //Dotnet
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.2")

    //Fcm
    implementation("com.google.firebase:firebase-admin:9.2.0")

    // Jwt
    compileOnly("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    testImplementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    testImplementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    testImplementation("io.jsonwebtoken:jjwt-api:0.12.6")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Logging
    implementation("org.springframework.boot:spring-boot-starter-logging")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("commons-validator:commons-validator:1.7")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.asyncer:r2dbc-mysql:1.0.4")
    testRuntimeOnly("com.mysql:mysql-connector-j")
    testCompileOnly("org.flywaydb:flyway-mysql")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("it.ozimov:embedded-redis:0.7.1")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

