// Requirement.kt - 需求聚合根
package com.lifetree.domain.model.requirement

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

class Requirement private constructor(
    val id: RequirementId,
    private var title: String,
    private var description: String,
    private var status: RequirementStatus,
    val createdBy: UserId,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: RequirementId,
            title: String,
            description: String,
            createdBy: UserId
        ): Requirement {
            require(title.isNotBlank()) { "Title cannot be blank" }

            val now = LocalDateTime.now()
            return Requirement(
                id = id,
                title = title,
                description = description,
                status = RequirementStatus.CREATED,
                createdBy = createdBy,
                createdAt = now,
                updatedAt = now
            )
        }

        internal fun reconstitute(
            id: RequirementId,
            title: String,
            description: String,
            status: RequirementStatus,
            createdBy: UserId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Requirement {
            return Requirement(
                id, title, description, status,
                createdBy, createdAt, updatedAt
            )
        }
    }

    fun getTitle(): String = title

    fun getDescription(): String = description

    fun getStatus(): RequirementStatus = status

    fun getUpdatedAt(): LocalDateTime = updatedAt

    fun updateTitle(newTitle: String) {
        require(newTitle.isNotBlank()) { "Title cannot be blank" }
        title = newTitle
        updatedAt = LocalDateTime.now()
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
        updatedAt = LocalDateTime.now()
    }

    fun updateStatus(newStatus: RequirementStatus) {
        // 可以在这里添加状态转换的业务规则
        status = newStatus
        updatedAt = LocalDateTime.now()
    }
}



