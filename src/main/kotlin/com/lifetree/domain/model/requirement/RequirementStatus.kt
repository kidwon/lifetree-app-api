// RequirementStatus.kt - 需求状态枚举
package com.lifetree.domain.model.requirement

enum class RequirementStatus {
    CREATED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

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
