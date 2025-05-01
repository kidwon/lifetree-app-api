package com.lifetree.domain.repository

import com.lifetree.domain.model.user.User
import com.lifetree.domain.model.user.UserId

interface UserRepository {
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findAll(): List<User>
    suspend fun save(user: User): User
    suspend fun delete(id: UserId): Boolean
    suspend fun emailExists(email: String): Boolean
}