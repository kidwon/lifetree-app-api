package com.lifetree.domain.model.user

enum class UserRole {
    USER,
    ADMIN;

    companion object {
        fun fromString(role: String): UserRole {
            return try {
                valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid UserRole: $role")
            }
        }
    }
}