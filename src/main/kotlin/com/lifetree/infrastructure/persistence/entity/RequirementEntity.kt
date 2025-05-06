// RequirementEntity.kt - 数据库实体
package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.RequirementStatus
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.*

data class RequirementEntity(
    val id: UUID,
    val title: String,
    val description: String,
    val status: String,
    val createdBy: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): Requirement {
        return Requirement.reconstitute(
            id = RequirementId(id),
            title = title,
            description = description,
            status = RequirementStatus.fromString(status),
            createdBy = UserId(createdBy),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(requirement: Requirement): RequirementEntity {
            return RequirementEntity(
                id = requirement.id.value,
                title = requirement.getTitle(),
                description = requirement.getDescription(),
                status = requirement.getStatus().name,
                createdBy = requirement.createdBy.value,
                createdAt = requirement.createdAt,
                updatedAt = requirement.getUpdatedAt()
            )
        }
    }
}