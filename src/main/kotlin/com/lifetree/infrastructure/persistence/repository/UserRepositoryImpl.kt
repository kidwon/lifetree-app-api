package com.lifetree.infrastructure.persistence.repository

import com.lifetree.domain.model.user.User
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.UserRepository
import com.lifetree.infrastructure.config.dbQuery
import com.lifetree.infrastructure.persistence.entity.UserEntity
import com.lifetree.infrastructure.persistence.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepositoryImpl : UserRepository {

    override suspend fun findById(id: UserId): User? = dbQuery {
        Users.select { Users.id eq id.value }
            .singleOrNull()
            ?.toUserEntity()
            ?.toDomain()
    }

    override suspend fun findByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email.lowercase() }
            .singleOrNull()
            ?.toUserEntity()
            ?.toDomain()
    }

    override suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll()
            .map { it.toUserEntity().toDomain() }
    }

    override suspend fun save(user: User): User = dbQuery {
        val entity = UserEntity.fromDomain(user)

        // 检查是否存在，决定更新还是插入
        val existingId = Users.select { Users.id eq user.id.value }
            .singleOrNull()
            ?.get(Users.id)

        if (existingId != null) {
            // 更新现有记录
            Users.update({ Users.id eq user.id.value }) {
                it[email] = entity.email
                it[passwordHash] = entity.passwordHash
                it[name] = entity.name
                it[role] = entity.role
                it[updatedAt] = entity.updatedAt
            }
        } else {
            // 插入新记录
            Users.insert {
                it[id] = entity.id
                it[email] = entity.email
                it[passwordHash] = entity.passwordHash
                it[name] = entity.name
                it[role] = entity.role
                it[createdAt] = entity.createdAt
                it[updatedAt] = entity.updatedAt
            }
        }

        // 返回保存后的对象
        user
    }

    override suspend fun delete(id: UserId): Boolean = dbQuery {
        val deletedRows = Users.deleteWhere { Users.id eq id.value }
        deletedRows > 0
    }

    override suspend fun emailExists(email: String): Boolean = dbQuery {
        Users.select { Users.email eq email.lowercase() }
            .count() > 0
    }

    // ResultSet 转换为 Entity 的扩展函数
    private fun ResultRow.toUserEntity(): UserEntity {
        return UserEntity(
            id = this[Users.id],
            email = this[Users.email],
            passwordHash = this[Users.passwordHash],
            name = this[Users.name],
            role = this[Users.role],
            createdAt = this[Users.createdAt],
            updatedAt = this[Users.updatedAt]
        )
    }
}