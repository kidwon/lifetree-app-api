package com.lifetree.infrastructure.persistence.entity

import com.lifetree.domain.model.user.User
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.user.UserRole
import java.time.LocalDateTime
import java.util.*

data class UserEntity(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val name: String,
    val role: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): User {
        val userId = UserId(id)
        val userRole = UserRole.fromString(role)

        return User.create(
            id = userId,
            email = email,
            passwordHash = passwordHash,
            name = name,
            role = userRole
        )
    }

    companion object {
        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                id = user.id.value,
                email = user.getEmail(),
                passwordHash = user.getPasswordHash(),
                name = user.getName(),
                role = user.getRole().name,
                createdAt = user.createdAt,
                updatedAt = user.getUpdatedAt()
            )
        }
    }
}