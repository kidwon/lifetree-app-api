// UpdateUserDto.kt in com.lifetree.application.dto.user package
package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDto(
    val name: String? = null,
    val email: String? = null
)