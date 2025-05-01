package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(
    val email: String,
    val password: String,
    val name: String
)