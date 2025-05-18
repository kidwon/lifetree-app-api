// 带申请信息的需求DTO (RequirementWithApplicationDto.kt) (添加协议字段)
package com.lifetree.application.dto.requirement

import com.lifetree.application.dto.user.UserBasicDto
import kotlinx.serialization.Serializable

@Serializable
data class RequirementWithApplicationDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val agreement: String?,
    val agreementButtonText: String?, // 新增协议按钮文本字段
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val applicant: UserBasicDto? = null,
    val applicationStatus: String? = null,
    val pendingApproval: Boolean = false,
    val applicationId: String? = null,
    val pendingApplicationsCount: Int = 0
)
