package com.lifetree.application.dto.result

import kotlinx.serialization.Serializable

@Serializable
data class UpdateResultDto(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null
)