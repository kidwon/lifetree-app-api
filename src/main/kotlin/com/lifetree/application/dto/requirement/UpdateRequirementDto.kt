// UpdateRequirementDto.kt - 更新需求DTO
package com.lifetree.application.dto.requirement
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequirementDto(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val agreement: String? = null,
    val agreementButtonText: String? = null // 新增协议按钮文本字段
)