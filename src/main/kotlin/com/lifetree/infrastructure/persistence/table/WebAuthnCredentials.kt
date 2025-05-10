// WebAuthnCredentials.kt - WebAuthn凭据数据库表
package com.lifetree.infrastructure.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * WebAuthn凭据数据库表定义
 */
object WebAuthnCredentials : Table() {
    val id = uuid("id").uniqueIndex()
    val userId = uuid("user_id").references(Users.id)
    val name = varchar("name", 255)
    val credentialId = text("credential_id")
    val publicKey = text("public_key")
    val counter = long("counter")
    val credentialFormat = varchar("credential_format", 50).default("packed")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)

    // 创建索引
    init {
        index(true, credentialId)
        index(false, userId)
    }
}




