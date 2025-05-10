// WebAuthnCredentialEntity.kt - WebAuthn凭据实体
package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.webauthn.WebAuthnCredential
import com.lifetree.domain.model.webauthn.WebAuthnCredentialId
import java.time.LocalDateTime
import java.util.*

/**
 * WebAuthn凭据数据库实体
 */
data class WebAuthnCredentialEntity(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val credentialId: String,
    val publicKey: String,
    val counter: Long,
    val credentialFormat: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): WebAuthnCredential {
        return WebAuthnCredential.create(
            id = WebAuthnCredentialId(id),
            userId = UserId(userId),
            name = name,
            credentialId = credentialId,
            publicKey = publicKey,
            counter = counter,
            credentialFormat = credentialFormat
        )
    }

    companion object {
        fun fromDomain(credential: WebAuthnCredential): WebAuthnCredentialEntity {
            return WebAuthnCredentialEntity(
                id = credential.id.value,
                userId = credential.userId.value,
                name = credential.name,
                credentialId = credential.getCredentialId(),
                publicKey = credential.getPublicKey(),
                counter = credential.getCounter(),
                credentialFormat = credential.getCredentialFormat(),
                createdAt = credential.createdAt,
                updatedAt = credential.getUpdatedAt()
            )
        }
    }
}