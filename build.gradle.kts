val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val hikariCpVersion: String by project
val koinVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}
group = "com.lifetree"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

//    mainClass.set("com.lifetree.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // 或使用 EXCLUDE 或 WARN
}

dependencies {
//    implementation("io.ktor:ktor-server-cors")
//    implementation("io.ktor:ktor-server-core")
//    implementation("io.ktor:ktor-server-content-negotiation")
//    implementation("io.ktor:ktor-serialization-kotlinx-json")
//    implementation("io.ktor:ktor-server-call-logging")
//    implementation("io.ktor:ktor-server-auth")
//    implementation("io.ktor:ktor-server-auth-jwt")
//    implementation("io.ktor:ktor-server-host-common")
//    implementation("io.ktor:ktor-server-status-pages")
//    implementation("io.ktor:ktor-server-resources")
//    implementation("io.ktor:ktor-server-sessions")
//    implementation("io.ktor:ktor-server-websockets")
//    implementation("io.ktor:ktor-server-netty")
//    implementation("ch.qos.logback:logback-classic:$logback_version")
//    implementation("io.ktor:ktor-server-config-yaml")
//    testImplementation("io.ktor:ktor-server-test-host")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Ktor Core
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-server-cors:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktorVersion}")
// Database
    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("com.zaxxer:HikariCP:${hikariCpVersion}")
// Dependency Injection
    implementation("io.insert-koin:koin-ktor:${koinVersion}")
    implementation("io.insert-koin:koin-logger-slf4j:${koinVersion}")
// Logging
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
// Testing
    testImplementation("io.ktor:ktor-server-test-host:${ktorVersion}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
}

kotlin {
    jvmToolchain(17) // 统一用Java 17（推荐Ktor官方用这个版本）
}

tasks.test {
    useJUnitPlatform()
}