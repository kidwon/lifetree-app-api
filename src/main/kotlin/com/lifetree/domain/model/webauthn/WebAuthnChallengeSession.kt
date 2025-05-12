package com.lifetree.domain.model.webauthn

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

/**
 * WebAuthn挑战会话模型
 * 用于存储和管理挑战值的会话信息
 */
data class WebAuthnChallengeSession(
    val challenge: String,
    val userId: UserId? = null,
    val username: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(5)
) {
    /**
     * 检查会话是否已过期
     */
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}