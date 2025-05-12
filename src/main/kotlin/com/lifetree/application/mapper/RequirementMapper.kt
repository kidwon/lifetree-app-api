// 需求映射器更新 (RequirementMapper.kt) (添加对协议字段的支持)

package com.lifetree.application.mapper

import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.RequirementWithApplicationDto
import com.lifetree.application.dto.user.UserBasicDto
import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.application.RequirementApplication
import com.lifetree.domain.model.user.User
import java.time.format.DateTimeFormatter

object RequirementMapper {
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    // 基本DTO转换
    fun toDto(requirement: Requirement): RequirementDto {
        return RequirementDto(
            id = requirement.id.toString(),
            title = requirement.getTitle(),
            description = requirement.getDescription(),
            status = requirement.getStatus().name,
            agreement = requirement.getAgreement(), // 添加协议字段
            createdBy = requirement.createdBy.toString(),
            createdAt = requirement.createdAt.format(dateTimeFormatter),
            updatedAt = requirement.getUpdatedAt().format(dateTimeFormatter)
        )
    }

    // 带申请信息的DTO转换
    fun toWithApplicationDto(
        requirement: Requirement,
        application: RequirementApplication?,
        user: User?,
        pendingApproval: Boolean,
        pendingApplicationsCount: Int = 0
    ): RequirementWithApplicationDto {
        val applicant = user?.let {
            UserBasicDto(
                id = it.id.toString(),
                name = it.getName(),
                email = it.getEmail()
            )
        }

        return RequirementWithApplicationDto(
            id = requirement.id.toString(),
            title = requirement.getTitle(),
            description = requirement.getDescription(),
            status = requirement.getStatus().name,
            agreement = requirement.getAgreement(), // 添加协议字段
            createdBy = requirement.createdBy.toString(),
            createdAt = requirement.createdAt.format(dateTimeFormatter),
            updatedAt = requirement.getUpdatedAt().format(dateTimeFormatter),
            applicant = applicant,
            applicationStatus = application?.getStatus()?.name,
            pendingApproval = pendingApproval,
            applicationId = application?.id?.toString(),
            pendingApplicationsCount = pendingApplicationsCount
        )
    }
}