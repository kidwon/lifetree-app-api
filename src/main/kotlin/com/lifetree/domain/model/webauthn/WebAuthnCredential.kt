// WebAuthnCredential.kt - WebAuthn凭据领域模型
package com.lifetree.domain.model.webauthn

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.*

/**
 * WebAuthn凭据领域模型
 */
class WebAuthnCredential private constructor(
    val id: WebAuthnCredentialId,
    val userId: UserId,
    val name: String,
    private val credentialId: String,
    private val publicKey: String,
    private val counter: Long,
    private val credentialFormat: String,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: WebAuthnCredentialId,
            userId: UserId,
            name: String,
            credentialId: String,
            publicKey: String,
            counter: Long = 0,
            credentialFormat: String = "packed"
        ): WebAuthnCredential {
            val now = LocalDateTime.now()
            return WebAuthnCredential(
                id = id,
                userId = userId,
                name = name,
                credentialId = credentialId,
                publicKey = publicKey,
                counter = counter,
                credentialFormat = credentialFormat,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun getCredentialId(): String = credentialId
    fun getPublicKey(): String = publicKey
    fun getCounter(): Long = counter
    fun getCredentialFormat(): String = credentialFormat
    fun getUpdatedAt(): LocalDateTime = updatedAt

    /**
     * 更新计数器
     */
    fun updateCounter(newCounter: Long) {
        require(newCounter > counter) { "新计数器值必须大于当前值" }
        updatedAt = LocalDateTime.now()
    }
}

/**
 * WebAuthn凭据ID值对象
 */
data class WebAuthnCredentialId(val value: UUID) {
    companion object {
        fun generate(): WebAuthnCredentialId = WebAuthnCredentialId(UUID.randomUUID())

        fun fromString(id: String): WebAuthnCredentialId {
            return try {
                WebAuthnCredentialId(UUID.fromString(id))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid WebAuthnCredentialId format")
            }
        }
    }

    override fun toString(): String = value.toString()
}
