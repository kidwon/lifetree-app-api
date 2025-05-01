// Result.kt - 结果聚合根
package com.lifetree.domain.model.result

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

class Result private constructor(
    val id: ResultId,
    private var title: String,
    private var description: String,
    private var status: ResultStatus,
    val relatedRequirementId: RequirementId?,
    val createdBy: UserId,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: ResultId,
            title: String,
            description: String,
            relatedRequirementId: RequirementId?,
            createdBy: UserId
        ): Result {
            require(title.isNotBlank()) { "Title cannot be blank" }

            val now = LocalDateTime.now()
            return Result(
                id = id,
                title = title,
                description = description,
                status = ResultStatus.DRAFT,
                relatedRequirementId = relatedRequirementId,
                createdBy = createdBy,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun getTitle(): String = title

    fun getDescription(): String = description

    fun getStatus(): ResultStatus = status

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

    fun updateStatus(newStatus: ResultStatus) {
        // 可以在这里添加状态转换的业务规则
        status = newStatus
        updatedAt = LocalDateTime.now()
    }
}