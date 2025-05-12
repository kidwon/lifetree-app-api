// 需求申请领域模型 (RequirementApplication.kt)
package com.lifetree.domain.model.requirement.application

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

class RequirementApplication private constructor(
    val id: ApplicationId,
    private val requirementId: RequirementId,
    private val applicantId: UserId,
    private var status: ApplicationStatus,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            requirementId: RequirementId,
            applicantId: UserId
        ): RequirementApplication {
            val now = LocalDateTime.now()
            return RequirementApplication(
                id = ApplicationId.generate(),
                requirementId = requirementId,
                applicantId = applicantId,
                status = ApplicationStatus.PENDING,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun getRequirementId(): RequirementId = requirementId

    fun getApplicantId(): UserId = applicantId

    fun getStatus(): ApplicationStatus = status

    fun getUpdatedAt(): LocalDateTime = updatedAt

    // 同意申请
    fun approve() {
        status = ApplicationStatus.APPROVED
        updatedAt = LocalDateTime.now()
    }

    // 拒绝申请
    fun reject() {
        status = ApplicationStatus.REJECTED
        updatedAt = LocalDateTime.now()
    }
}

