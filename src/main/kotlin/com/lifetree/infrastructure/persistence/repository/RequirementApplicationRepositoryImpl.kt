// 需求申请仓储实现 (RequirementApplicationRepositoryImpl.kt)
package com.lifetree.infrastructure.persistence.repository

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.application.ApplicationId
import com.lifetree.domain.model.requirement.application.RequirementApplication
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.RequirementApplicationRepository
import com.lifetree.infrastructure.config.dbQuery
import com.lifetree.infrastructure.persistence.entity.RequirementApplicationEntity
import com.lifetree.infrastructure.persistence.table.RequirementApplications
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RequirementApplicationRepositoryImpl : RequirementApplicationRepository {

    override suspend fun findById(id: ApplicationId): RequirementApplication? = dbQuery {
        RequirementApplications.select { RequirementApplications.id eq id.value }
            .singleOrNull()
            ?.toApplicationEntity()
            ?.toDomain()
    }

    override suspend fun findByRequirementId(requirementId: RequirementId): List<RequirementApplication> = dbQuery {
        RequirementApplications.select { RequirementApplications.requirementId eq requirementId.value }
            .map { it.toApplicationEntity().toDomain() }
    }

    override suspend fun findByApplicantId(applicantId: UserId): List<RequirementApplication> = dbQuery {
        RequirementApplications.select { RequirementApplications.applicantId eq applicantId.value }
            .map { it.toApplicationEntity().toDomain() }
    }

    override suspend fun save(application: RequirementApplication): RequirementApplication = dbQuery {
        val entity = RequirementApplicationEntity.fromDomain(application)

        // 检查是否存在，决定更新还是插入
        val existingId = RequirementApplications.select { RequirementApplications.id eq application.id.value }
            .singleOrNull()
            ?.get(RequirementApplications.id)

        if (existingId != null) {
            // 更新现有记录
            RequirementApplications.update({ RequirementApplications.id eq application.id.value }) {
                it[status] = entity.status
                it[updatedAt] = entity.updatedAt
            }
        } else {
            // 插入新记录
            RequirementApplications.insert {
                it[id] = entity.id
                it[requirementId] = entity.requirementId
                it[applicantId] = entity.applicantId
                it[status] = entity.status
                it[createdAt] = entity.createdAt
                it[updatedAt] = entity.updatedAt
            }
        }

        // 返回保存后的对象
        application
    }

    override suspend fun delete(id: ApplicationId): Boolean = dbQuery {
        val deletedRows = RequirementApplications.deleteWhere { RequirementApplications.id eq id.value }
        deletedRows > 0
    }

    // ResultSet 转换为 Entity 的扩展函数
    private fun ResultRow.toApplicationEntity(): RequirementApplicationEntity {
        return RequirementApplicationEntity(
            id = this[RequirementApplications.id],
            requirementId = this[RequirementApplications.requirementId],
            applicantId = this[RequirementApplications.applicantId],
            status = this[RequirementApplications.status],
            createdAt = this[RequirementApplications.createdAt],
            updatedAt = this[RequirementApplications.updatedAt]
        )
    }
}