plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.flywaydb.flyway") version "10.13.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "ru.forbidden.sequence"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}


val `flyway-database-version` = "10.17.1"
val `flyway-core-version` = "10.10.0"
val `testcontainers-version` = "1.19.8"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.data:spring-data-jdbc")
//    implementation("com.h2database:h2")
    implementation("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core:$`flyway-core-version`")
    implementation("org.flywaydb:flyway-database-postgresql:$`flyway-database-version`")


    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    jvmArgs = listOf("-Dlogging.level.root=INFO", "-Dlogging.level.ru.forbidden.sequence.testproject=DEBUG")
    useJUnitPlatform()
}
