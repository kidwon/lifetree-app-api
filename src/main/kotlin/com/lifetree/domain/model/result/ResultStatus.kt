package com.lifetree.domain.model.result

enum class ResultStatus {
    DRAFT,
    COMPLETED,
    ARCHIVED,
    REJECTED;

    companion object {
        fun fromString(status: String): ResultStatus {
            return try {
                valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid ResultStatus: $status")
            }
        }
    }
}