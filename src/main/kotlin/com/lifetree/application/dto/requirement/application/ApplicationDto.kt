// 申请DTO (ApplicationDto.kt)
package com.lifetree.application.dto.requirement.application

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val id: String,
    val requirementId: String,
    val applicantId: String,
    val applicantName: String,
    val applicantEmail: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)