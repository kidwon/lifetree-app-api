package com.lifetree.domain.model.webauthn

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.*

/**
 * WebAuthn挑战值对象
 */
data class WebAuthnChallenge(
    val value: String,
    val userId: UserId? = null,
    val username: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(5)
) {
    companion object {
        /**
         * 生成新的随机挑战
         */
        fun generate(userId: UserId? = null, username: String? = null): WebAuthnChallenge {
            val random = ByteArray(32)
            Random().nextBytes(random)
            val challenge = Base64.getEncoder().encodeToString(random)

            return WebAuthnChallenge(
                value = challenge,
                userId = userId,
                username = username
            )
        }
    }

    /**
     * 检查挑战是否过期
     */
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}