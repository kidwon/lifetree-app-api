// RequirementEntity.kt - 数据库实体
package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.RequirementStatus
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.UUID

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
        val requirementId = RequirementId(id)
        val userId = UserId(createdBy)
        val requirementStatus = RequirementStatus.fromString(status)

        return Requirement.create(
            id = requirementId,
            title = title,
            description = description,
            createdBy = userId
        ).apply {
            updateStatus(requirementStatus)
        }
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