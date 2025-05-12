// WebAuthnRoutes.kt - WebAuthn路由
package com.lifetree.presentation.route

import com.lifetree.application.dto.webauthn.*
import com.lifetree.application.service.WebAuthnApplicationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置WebAuthn路由
 */
fun Route.webAuthnRoutes() {
    val webAuthnService by inject<WebAuthnApplicationService>()

    route("/webauthn") {
        // 获取注册选项，需要已认证的用户
        authenticate {
            post("/registration/options") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("id").asString()
                val request = call.receive<RegistrationOptionsRequestDto>()

                val options = webAuthnService.generateRegistrationOptions(userId, request)
                if (options != null) {
                    call.respond(options)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "无法生成注册选项"))
                }
            }

            // 完成注册
            post("/registration/result") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.payload.getClaim("id").asString()
                val result = call.receive<RegistrationResultDto>()

                val response = webAuthnService.verifyRegistration(userId, result)
                if (response != null) {
                    call.respond(response)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "注册验证失败"))
                }
            }
        }

        // 获取认证选项，不需要认证
        post("/authentication/options") {
            val request = call.receive<AuthenticationOptionsRequestDto>()

            val options = webAuthnService.generateAuthenticationOptions(request)
            call.respond(options)
        }

        // 完成认证，不需要认证
        post("/authentication/result") {
            val result = call.receive<AuthenticationResultDto>()

            val response = webAuthnService.verifyAuthentication(result)
            if (response != null) {
                call.respond(response)
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "认证失败"))
            }
        }
    }
}

/**
 * 在主路由中添加WebAuthn路由
 */
fun Application.configureWebAuthnRouting() {
    routing {
        webAuthnRoutes()
    }
}