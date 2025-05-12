// 用户基本信息DTO (UserBasicDto.kt)
package com.lifetree.application.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserBasicDto(
    val id: String,
    val name: String,
    val email: String
)