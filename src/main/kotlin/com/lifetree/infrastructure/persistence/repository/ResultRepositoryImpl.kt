package com.lifetree.infrastructure.persistence.repository

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.Result
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.ResultRepository
import com.lifetree.infrastructure.config.dbQuery
import com.lifetree.infrastructure.persistence.entity.ResultEntity
import com.lifetree.infrastructure.persistence.table.Results
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ResultRepositoryImpl : ResultRepository {

    override suspend fun findById(id: ResultId): Result? = dbQuery {
        Results.select { Results.id eq id.value }
            .singleOrNull()
            ?.toResultEntity()
            ?.toDomain()
    }

    override suspend fun findAll(): List<Result> = dbQuery {
        Results.selectAll()
            .map { it.toResultEntity().toDomain() }
    }

    override suspend fun findByCreatedBy(userId: UserId): List<Result> = dbQuery {
        Results.select { Results.createdBy eq userId.value }
            .map { it.toResultEntity().toDomain() }
    }

    override suspend fun findByRequirementId(requirementId: RequirementId): List<Result> = dbQuery {
        Results.select { Results.relatedRequirementId eq requirementId.value }
            .map { it.toResultEntity().toDomain() }
    }

    override suspend fun save(result: Result): Result = dbQuery {
        val entity = ResultEntity.fromDomain(result)

        // 检查是否存在，决定更新还是插入
        val existingId = Results.select { Results.id eq result.id.value }
            .singleOrNull()
            ?.get(Results.id)

        if (existingId != null) {
            // 更新现有记录
            Results.update({ Results.id eq result.id.value }) {
                it[title] = entity.title
                it[description] = entity.description
                it[status] = entity.status
                it[updatedAt] = entity.updatedAt
            }
        } else {
            // 插入新记录
            Results.insert {
                it[id] = entity.id
                it[title] = entity.title
                it[description] = entity.description
                it[status] = entity.status
                it[relatedRequirementId] = entity.relatedRequirementId
                it[createdBy] = entity.createdBy
                it[createdAt] = entity.createdAt
                it[updatedAt] = entity.updatedAt
            }
        }

        // 返回保存后的对象
        result
    }

    override suspend fun delete(id: ResultId): Boolean = dbQuery {
        val deletedRows = Results.deleteWhere { Results.id eq id.value }
        deletedRows > 0
    }

    // ResultSet 转换为 Entity 的扩展函数
    private fun ResultRow.toResultEntity(): ResultEntity {
        return ResultEntity(
            id = this[Results.id],
            title = this[Results.title],
            description = this[Results.description],
            status = this[Results.status],
            relatedRequirementId = this[Results.relatedRequirementId],
            createdBy = this[Results.createdBy],
            createdAt = this[Results.createdAt],
            updatedAt = this[Results.updatedAt]
        )
    }
}