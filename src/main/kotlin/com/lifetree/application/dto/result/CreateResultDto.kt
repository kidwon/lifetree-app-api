package com.lifetree.application.dto.result

import kotlinx.serialization.Serializable

@Serializable
data class CreateResultDto(
    val title: String,
    val description: String,
    val relatedRequirementId: String? = null
)