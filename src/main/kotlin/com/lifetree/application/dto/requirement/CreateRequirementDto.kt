// CreateRequirementDto.kt - 创建需求DTO
package com.lifetree.application.dto.requirement

import kotlinx.serialization.Serializable

@Serializable
data class CreateRequirementDto(
    val title: String,
    val description: String,
    val agreement: String? = null
)