package com.lifetree.domain.model.requirement.application

enum class ApplicationStatus {
    PENDING,    // 等待确认
    APPROVED,   // 已同意
    REJECTED;   // 已拒绝

    companion object {
        fun fromString(status: String): ApplicationStatus {
            return try {
                valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid ApplicationStatus: $status")
            }
        }
    }
}