// JwtProvider.kt - JWT提供者
package com.lifetree.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.lifetree.domain.model.user.User
import io.ktor.server.config.*
import java.util.*

class JwtProvider(config: ApplicationConfig) {
    private val secret = config.property("jwt.secret").getString()
    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()
    val realm = config.property("jwt.realm").getString()
    private val expirationMillis = config.property("jwt.expiration").getString().toLong()

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    /**
     * 为用户生成JWT令牌
     */
    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("id", user.id.toString())
            .withClaim("email", user.getEmail())
            .withClaim("role", user.getRole().name)
            .withExpiresAt(getExpiration())
            .sign(Algorithm.HMAC256(secret))
    }

    /**
     * 计算令牌过期时间
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + expirationMillis)
}