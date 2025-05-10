// WebAuthnCredentialRepositoryImpl.kt - WebAuthn凭据仓储实现
package com.lifetree.infrastructure.persistence.repository

import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.webauthn.WebAuthnCredential
import com.lifetree.domain.model.webauthn.WebAuthnCredentialId
import com.lifetree.domain.repository.WebAuthnCredentialRepository
import com.lifetree.infrastructure.config.dbQuery
import com.lifetree.infrastructure.persistence.entity.WebAuthnCredentialEntity
import com.lifetree.infrastructure.persistence.table.WebAuthnCredentials
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * WebAuthn凭据仓储实现
 */
class WebAuthnCredentialRepositoryImpl : WebAuthnCredentialRepository {

    override suspend fun findById(id: WebAuthnCredentialId): WebAuthnCredential? = dbQuery {
        WebAuthnCredentials.select { WebAuthnCredentials.id eq id.value }
            .singleOrNull()
            ?.toCredentialEntity()
            ?.toDomain()
    }

    override suspend fun findByCredentialId(credentialId: String): WebAuthnCredential? = dbQuery {
        WebAuthnCredentials.select { WebAuthnCredentials.credentialId eq credentialId }
            .singleOrNull()
            ?.toCredentialEntity()
            ?.toDomain()
    }

    override suspend fun findByUserId(userId: UserId): List<WebAuthnCredential> = dbQuery {
        WebAuthnCredentials.select { WebAuthnCredentials.userId eq userId.value }
            .map { it.toCredentialEntity().toDomain() }
    }

    override suspend fun save(credential: WebAuthnCredential): WebAuthnCredential = dbQuery {
        val entity = WebAuthnCredentialEntity.fromDomain(credential)

        // 检查是否存在，决定更新还是插入
        val existingId = WebAuthnCredentials.select { WebAuthnCredentials.id eq credential.id.value }
            .singleOrNull()
            ?.get(WebAuthnCredentials.id)

        if (existingId != null) {
            // 更新现有记录
            WebAuthnCredentials.update({ WebAuthnCredentials.id eq credential.id.value }) {
                it[name] = entity.name
                it[counter] = entity.counter
                it[updatedAt] = entity.updatedAt
            }
        } else {
            // 插入新记录
            WebAuthnCredentials.insert {
                it[id] = entity.id
                it[userId] = entity.userId
                it[name] = entity.name
                it[credentialId] = entity.credentialId
                it[publicKey] = entity.publicKey
                it[counter] = entity.counter
                it[credentialFormat] = entity.credentialFormat
                it[createdAt] = entity.createdAt
                it[updatedAt] = entity.updatedAt
            }
        }

        // 返回保存后的对象
        credential
    }

    override suspend fun delete(id: WebAuthnCredentialId): Boolean = dbQuery {
        val deletedRows = WebAuthnCredentials.deleteWhere { WebAuthnCredentials.id eq id.value }
        deletedRows > 0
    }

    override suspend fun deleteByUserId(userId: UserId): Int = dbQuery {
        WebAuthnCredentials.deleteWhere { WebAuthnCredentials.userId eq userId.value }
    }

    // ResultSet 转换为 Entity 的扩展函数
    private fun ResultRow.toCredentialEntity(): WebAuthnCredentialEntity {
        return WebAuthnCredentialEntity(
            id = this[WebAuthnCredentials.id],
            userId = this[WebAuthnCredentials.userId],
            name = this[WebAuthnCredentials.name],
            credentialId = this[WebAuthnCredentials.credentialId],
            publicKey = this[WebAuthnCredentials.publicKey],
            counter = this[WebAuthnCredentials.counter],
            credentialFormat = this[WebAuthnCredentials.credentialFormat],
            createdAt = this[WebAuthnCredentials.createdAt],
            updatedAt = this[WebAuthnCredentials.updatedAt]
        )
    }
}