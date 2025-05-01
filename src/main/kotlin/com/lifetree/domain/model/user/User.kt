package com.lifetree.domain.model.user

import java.time.LocalDateTime

class User private constructor(
    val id: UserId,
    private var email: String,
    private var passwordHash: String,
    private var name: String,
    private var role: UserRole,
    val createdAt: LocalDateTime,
    private var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: UserId,
            email: String,
            passwordHash: String,
            name: String,
            role: UserRole = UserRole.USER
        ): User {
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }
            require(name.isNotBlank()) { "Name cannot be blank" }

            val now = LocalDateTime.now()
            return User(
                id = id,
                email = email.lowercase(),
                passwordHash = passwordHash,
                name = name,
                role = role,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun getEmail(): String = email

    fun getPasswordHash(): String = passwordHash

    fun getName(): String = name

    fun getRole(): UserRole = role

    fun getUpdatedAt(): LocalDateTime = updatedAt

    fun updateEmail(newEmail: String) {
        require(newEmail.isNotBlank()) { "Email cannot be blank" }
        email = newEmail.lowercase()
        updatedAt = LocalDateTime.now()
    }

    fun updatePassword(newPasswordHash: String) {
        require(newPasswordHash.isNotBlank()) { "Password hash cannot be blank" }
        passwordHash = newPasswordHash
        updatedAt = LocalDateTime.now()
    }

    fun updateName(newName: String) {
        require(newName.isNotBlank()) { "Name cannot be blank" }
        name = newName
        updatedAt = LocalDateTime.now()
    }

    fun updateRole(newRole: UserRole) {
        role = newRole
        updatedAt = LocalDateTime.now()
    }
}