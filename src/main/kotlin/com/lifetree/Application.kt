// Application.kt - 应用入口点
package com.lifetree

import com.lifetree.infrastructure.config.configureDatabases
import com.lifetree.infrastructure.config.configureKoin
import com.lifetree.infrastructure.config.configureSecurity
import com.lifetree.presentation.route.configureRouting
import com.lifetree.presentation.route.configureWebAuthnRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.Json

/*fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}*/

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    try {
        val dbConfig = environment.config.config("database")
        println("Database config found: $dbConfig")
    } catch (e: Exception) {
        println("Error loading database config: ${e.message}")
        e.printStackTrace()
    }

    // 配置 JSON 序列化
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // 配置 CORS
    install(CORS) {
        anyHost() // 允许任何主机，生产环境应该更具体地限制
        allowHeader("Content-Type")
        allowHeader("Authorization")
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
    }

    // 配置依赖注入
    configureKoin()

    // 配置数据库
    configureDatabases()

    // 配置安全
    configureSecurity()

    // 配置路由
    configureRouting()

    // 配置WebAuthn路由
    configureWebAuthnRouting()
}