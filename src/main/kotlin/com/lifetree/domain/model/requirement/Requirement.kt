// Requirement.kt - 需求聚合根 (添加协议按钮文本字段)
package com.lifetree.domain.model.requirement

import com.lifetree.domain.model.user.UserId
import java.time.LocalDateTime

class Requirement private constructor(
    val id: RequirementId,
    private var title: String,
    private var description: String,
    private var status: RequirementStatus,
    private var agreement: String?, // 协议内容字段
    private var agreementButtonText: String?, // 新增协议按钮文本字段
    val createdBy: UserId,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        // 默认协议按钮文本
        const val DEFAULT_AGREEMENT_BUTTON_TEXT = "我已阅读并同意此协议"

        // 协议按钮文本最大长度
        const val MAX_AGREEMENT_BUTTON_TEXT_LENGTH = 20

        fun create(
            id: RequirementId,
            title: String,
            description: String,
            agreement: String? = null,
            agreementButtonText: String? = null, // 新增参数
            createdBy: UserId
        ): Requirement {
            require(title.isNotBlank()) { "Title cannot be blank" }

            // 验证协议按钮文本长度
            if (agreementButtonText != null) {
                require(agreementButtonText.isNotBlank()) { "Agreement button text cannot be blank" }
                require(agreementButtonText.length <= MAX_AGREEMENT_BUTTON_TEXT_LENGTH) {
                    "Agreement button text cannot exceed $MAX_AGREEMENT_BUTTON_TEXT_LENGTH characters"
                }
            }

            val now = LocalDateTime.now()
            return Requirement(
                id = id,
                title = title,
                description = description,
                status = RequirementStatus.CREATED,
                agreement = agreement,
                agreementButtonText = agreementButtonText,
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
            agreementButtonText: String?,
            createdBy: UserId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Requirement {
            return Requirement(
                id, title, description, status, agreement, agreementButtonText,
                createdBy, createdAt, updatedAt
            )
        }
    }

    fun getTitle(): String = title

    fun getDescription(): String = description

    fun getStatus(): RequirementStatus = status

    fun getAgreement(): String? = agreement

    // 获取协议按钮文本，如果为null则返回默认值
    fun getAgreementButtonText(): String = agreementButtonText ?: DEFAULT_AGREEMENT_BUTTON_TEXT

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
        status = newStatus
        updatedAt = LocalDateTime.now()
    }

    fun updateAgreement(newAgreement: String?) {
        agreement = newAgreement
        updatedAt = LocalDateTime.now()
    }

    // 新增更新协议按钮文本方法
    fun updateAgreementButtonText(newButtonText: String?) {
        // 如果新文本为null，直接设置为null并返回
        if (newButtonText == null) {
            agreementButtonText = null
            updatedAt = LocalDateTime.now()
            return
        }

        // 验证新的按钮文本
        require(newButtonText.isNotBlank()) { "Agreement button text cannot be blank" }
        require(newButtonText.length <= MAX_AGREEMENT_BUTTON_TEXT_LENGTH) {
            "Agreement button text cannot exceed $MAX_AGREEMENT_BUTTON_TEXT_LENGTH characters"
        }

        agreementButtonText = newButtonText
        updatedAt = LocalDateTime.now()
    }
}