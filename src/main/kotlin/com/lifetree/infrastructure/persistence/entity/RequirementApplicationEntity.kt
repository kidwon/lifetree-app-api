// 需求申请实体 (RequirementApplicationEntity.kt)
package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.application.ApplicationId
import com.lifetree.domain.model.requirement.application.ApplicationStatus
import com.lifetree.domain.model.requirement.application.RequirementApplication
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime
import java.util.*

data class RequirementApplicationEntity(
    val id: UUID,
    val requirementId: UUID,
    val applicantId: UUID,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): RequirementApplication {
        val applicationId = ApplicationId(id)
        val reqId = RequirementId(requirementId)
        val userId = UserId(applicantId)
        val applicationStatus = ApplicationStatus.fromString(status)

        val application = RequirementApplication.create(
            requirementId = reqId,
            applicantId = userId
        )

        // 恢复原始状态
        val field = RequirementApplication::class.java.getDeclaredField("status")
        field.isAccessible = true
        field.set(application, applicationStatus)

        // 恢复时间戳
        val idField = RequirementApplication::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(application, applicationId)

        val createdAtField = RequirementApplication::class.java.getDeclaredField("createdAt")
        createdAtField.isAccessible = true
        createdAtField.set(application, createdAt)

        val updatedAtField = RequirementApplication::class.java.getDeclaredField("updatedAt")
        updatedAtField.isAccessible = true
        updatedAtField.set(application, updatedAt)

        return application
    }

    companion object {
        fun fromDomain(application: RequirementApplication): RequirementApplicationEntity {
            return RequirementApplicationEntity(
                id = application.id.value,
                requirementId = application.getRequirementId().value,
                applicantId = application.getApplicantId().value,
                status = application.getStatus().name,
                createdAt = application.createdAt,
                updatedAt = application.getUpdatedAt()
            )
        }
    }
}
