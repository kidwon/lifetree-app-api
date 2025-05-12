package com.lifetree.domain.model.requirement

enum class RequirementStatus {
    CREATED,       // 已创建
    CONFIRMING,    // 确认中 (新增状态)
    IN_PROGRESS,   // 进行中
    COMPLETED,     // 已完成
    CANCELLED;     // 已取消

    companion object {
        fun fromString(status: String): RequirementStatus {
            return try {
                valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid RequirementStatus: $status")
            }
        }
    }
}