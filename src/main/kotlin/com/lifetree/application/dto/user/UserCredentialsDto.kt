package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentialsDto(
    val email: String,
    val password: String
)