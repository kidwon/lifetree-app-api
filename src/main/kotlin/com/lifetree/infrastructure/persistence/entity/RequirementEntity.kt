// RequirementEntity.kt - 数据库实体 (添加协议按钮文本字段)
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
    val agreement: String?, // 协议内容字段
    val agreementButtonText: String?, // 新增协议按钮文本字段
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
            agreement = agreement,
            agreementButtonText = agreementButtonText,
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
                agreement = requirement.getAgreement(),
                agreementButtonText = requirement.getAgreementButtonText().takeIf { it != Requirement.DEFAULT_AGREEMENT_BUTTON_TEXT },
                createdBy = requirement.createdBy.value,
                createdAt = requirement.createdAt,
                updatedAt = requirement.getUpdatedAt()
            )
        }
    }
}