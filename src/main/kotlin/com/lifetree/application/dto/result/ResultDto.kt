package com.lifetree.application.dto.result

import kotlinx.serialization.Serializable

@Serializable
data class ResultDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val relatedRequirementId: String?,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)