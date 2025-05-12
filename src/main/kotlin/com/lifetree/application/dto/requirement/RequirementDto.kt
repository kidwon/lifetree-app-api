// RequirementDto.kt - 需求DTO (添加协议字段)
package com.lifetree.application.dto.requirement

import kotlinx.serialization.Serializable

@Serializable
data class RequirementDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val agreement: String?, // 新增协议字段
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)