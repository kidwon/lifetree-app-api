package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.Result
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.result.ResultStatus
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.UUID

data class ResultEntity(
    val id: UUID,
    val title: String,
    val description: String,
    val status: String,
    val relatedRequirementId: UUID?,
    val createdBy: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): Result {
        val resultId = ResultId(id)
        val userId = UserId(createdBy)
        val resultStatus = ResultStatus.fromString(status)
        val requirementId = relatedRequirementId?.let { RequirementId(it) }

        return Result.create(
            id = resultId,
            title = title,
            description = description,
            relatedRequirementId = requirementId,
            createdBy = userId
        ).apply {
            updateStatus(resultStatus)
        }
    }

    companion object {
        fun fromDomain(result: Result): ResultEntity {
            return ResultEntity(
                id = result.id.value,
                title = result.getTitle(),
                description = result.getDescription(),
                status = result.getStatus().name,
                relatedRequirementId = result.relatedRequirementId?.value,
                createdBy = result.createdBy.value,
                createdAt = result.createdAt,
                updatedAt = result.getUpdatedAt()
            )
        }
    }
}