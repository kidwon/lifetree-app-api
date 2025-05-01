package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val user: UserDto,
    val token: String
)