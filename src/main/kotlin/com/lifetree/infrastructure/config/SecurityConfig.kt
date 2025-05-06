package com.lifetree.infrastructure.config

import com.lifetree.infrastructure.security.JwtProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtProvider: JwtProvider by inject()

    install(Authentication) {
        jwt {
            verifier(jwtProvider.verifier)
            realm = jwtProvider.realm

            validate { credential ->
                // 验证JWT是否有效
                if (credential.payload.getClaim("id").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "认证令牌无效或已过期")
            }
        }
    }
}



