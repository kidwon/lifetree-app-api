// Requirement.kt - 需求聚合根 (添加协议字段)
package com.lifetree.domain.model.requirement

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

class Requirement private constructor(
    val id: RequirementId,
    private var title: String,
    private var description: String,
    private var status: RequirementStatus,
    private var agreement: String?, // 新增协议字段
    val createdBy: UserId,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: RequirementId,
            title: String,
            description: String,
            agreement: String? = null, // 可选协议参数
            createdBy: UserId
        ): Requirement {
            require(title.isNotBlank()) { "Title cannot be blank" }

            val now = LocalDateTime.now()
            return Requirement(
                id = id,
                title = title,
                description = description,
                status = RequirementStatus.CREATED,
                agreement = agreement,
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
            agreement: String?,
            createdBy: UserId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Requirement {
            return Requirement(
                id, title, description, status, agreement,
                createdBy, createdAt, updatedAt
            )
        }
    }

    fun getTitle(): String = title

    fun getDescription(): String = description

    fun getStatus(): RequirementStatus = status

    fun getAgreement(): String? = agreement

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

    // 新增更新协议方法
    fun updateAgreement(newAgreement: String?) {
        agreement = newAgreement
        updatedAt = LocalDateTime.now()
    }
}