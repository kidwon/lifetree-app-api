// WebAuthnCredentialRepository.kt - WebAuthn凭据仓储接口
package com.lifetree.domain.repository

import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.webauthn.WebAuthnCredential
import com.lifetree.domain.model.webauthn.WebAuthnCredentialId

/**
 * WebAuthn凭据仓储接口
 */
interface WebAuthnCredentialRepository {
    suspend fun findById(id: WebAuthnCredentialId): WebAuthnCredential?
    suspend fun findByCredentialId(credentialId: String): WebAuthnCredential?
    suspend fun findByUserId(userId: UserId): List<WebAuthnCredential>
    suspend fun save(credential: WebAuthnCredential): WebAuthnCredential
    suspend fun delete(id: WebAuthnCredentialId): Boolean
    suspend fun deleteByUserId(userId: UserId): Int
}