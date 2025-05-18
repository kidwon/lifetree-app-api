package com.lifetree.application.dto.requirement

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAgreementRequestDto(
    val agreement: String?,
    val agreementButtonText: String? = null // 新增协议按钮文本字段
)