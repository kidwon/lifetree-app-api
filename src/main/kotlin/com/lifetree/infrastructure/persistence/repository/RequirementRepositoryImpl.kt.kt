// RequirementRepositoryImpl.kt - 仓储实现
package com.lifetree.infrastructure.persistence.repository

import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.RequirementRepository
import com.lifetree.infrastructure.config.dbQuery
import com.lifetree.infrastructure.persistence.entity.RequirementEntity
import com.lifetree.infrastructure.persistence.table.Requirements
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RequirementRepositoryImpl : RequirementRepository {

    override suspend fun findById(id: RequirementId): Requirement? = dbQuery {
        Requirements.select { Requirements.id eq id.value }
            .singleOrNull()
            ?.toRequirementEntity()
            ?.toDomain()
    }

    override suspend fun findAll(): List<Requirement> = dbQuery {
        Requirements.selectAll()
            .map { it.toRequirementEntity().toDomain() }
    }

    override suspend fun findByCreatedBy(userId: UserId): List<Requirement> = dbQuery {
        Requirements.select { Requirements.createdBy eq userId.value }
            .map { it.toRequirementEntity().toDomain() }
    }

    override suspend fun save(requirement: Requirement): Requirement = dbQuery {
        val entity = RequirementEntity.fromDomain(requirement)

        // 检查是否存在，决定更新还是插入
        val existingId = Requirements.select { Requirements.id eq requirement.id.value }
            .singleOrNull()
            ?.get(Requirements.id)

        if (existingId != null) {
            // 更新现有记录
            Requirements.update({ Requirements.id eq requirement.id.value }) {
                it[title] = entity.title
                it[description] = entity.description
                it[status] = entity.status
                it[updatedAt] = entity.updatedAt
            }
        } else {
            // 插入新记录
            Requirements.insert {
                it[id] = entity.id
                it[title] = entity.title
                it[description] = entity.description
                it[status] = entity.status
                it[createdBy] = entity.createdBy
                it[createdAt] = entity.createdAt
                it[updatedAt] = entity.updatedAt
            }
        }

        // 返回保存后的对象
        requirement
    }

    override suspend fun delete(id: RequirementId): Boolean = dbQuery {
        val deletedRows = Requirements.deleteWhere { Requirements.id eq id.value }
        deletedRows > 0
    }

    // ResultSet 转换为 Entity 的扩展函数
    private fun ResultRow.toRequirementEntity(): RequirementEntity {
        return RequirementEntity(
            id = this[Requirements.id],
            title = this[Requirements.title],
            description = this[Requirements.description],
            status = this[Requirements.status],
            createdBy = this[Requirements.createdBy],
            createdAt = this[Requirements.createdAt],
            updatedAt = this[Requirements.updatedAt]
        )
    }
}