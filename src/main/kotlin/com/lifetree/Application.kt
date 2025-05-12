// Application.kt - 应用入口点
package com.lifetree

import com.lifetree.infrastructure.config.configureDatabases
import com.lifetree.infrastructure.config.configureKoin
import com.lifetree.infrastructure.config.configureSecurity
import com.lifetree.presentation.route.configureRouting
import com.lifetree.presentation.route.configureWebAuthnRouting
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
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
        // 允许所有域
        anyHost()
        // 允许前端域名访问
        // 允许从本地开发服务器访问
        allowHost("localhost:8080", schemes = listOf("https", "http"))
        allowHost("localhost:8081", schemes = listOf("https", "http"))
        allowHost("127.0.0.1:8081", schemes = listOf("https", "http"))
        allowHost("127.0.0.1:8080", schemes = listOf("https", "http"))

        // 允许从生产前端域名访问
        allowHost("frp-cup.com:44058", schemes = listOf("https"))
        allowHost("frp-cup.com:52701", schemes = listOf("https"))
        allowHost("www.u252116.nyat.app:52701/", schemes = listOf("https"))
        allowHost("api.u252116.nyat.app:44058/", schemes = listOf("https"))

        // 如果你还有其他需要访问的域名，也要添加进来
        // 比如你的 GitHub Pages 域名或本kidwon开发域名
        allowHost("kidwon.github.io", schemes = listOf("https"))

        // 允许所有方法
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)

        // 允许所有头信息
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader("*")

        allowCredentials = true
        maxAgeInSeconds = 3600

        // 允许的响应头（如果你的前端需要访问自定义响应头）
        allowHeadersPrefixed("X-") // 允许所有 X- 开头的自定义头
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