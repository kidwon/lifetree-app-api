package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)