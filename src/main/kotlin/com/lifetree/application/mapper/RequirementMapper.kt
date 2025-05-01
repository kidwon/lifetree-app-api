// RequirementMapper.kt - 需求映射器
package com.lifetree.application.mapper

import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.domain.model.requirement.Requirement
import java.time.format.DateTimeFormatter

object RequirementMapper {
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    fun toDto(requirement: Requirement): RequirementDto {
        return RequirementDto(
            id = requirement.id.toString(),
            title = requirement.getTitle(),
            description = requirement.getDescription(),
            status = requirement.getStatus().name,
            createdBy = requirement.createdBy.toString(),
            createdAt = requirement.createdAt.format(dateTimeFormatter),
            updatedAt = requirement.getUpdatedAt().format(dateTimeFormatter)
        )
    }
}