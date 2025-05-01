// RequirementDto.kt - 需求DTO
package com.lifetree.application.dto.requirement

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class RequirementDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)







