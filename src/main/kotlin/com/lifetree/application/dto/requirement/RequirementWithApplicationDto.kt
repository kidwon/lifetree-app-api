// 带申请信息的需求DTO (RequirementWithApplicationDto.kt)
package com.lifetree.application.dto.requirement

import com.lifetree.application.dto.user.UserBasicDto
import kotlinx.serialization.Serializable

@Serializable
data class RequirementWithApplicationDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val applicant: UserBasicDto? = null,
    val applicationStatus: String? = null,
    val pendingApproval: Boolean = false,
    val applicationId: String? = null,  // 新增申请ID字段
    val pendingApplicationsCount: Int = 0  // 新增待处理申请数量字段
)