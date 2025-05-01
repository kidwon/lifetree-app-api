package com.lifetree.application.mapper

import com.lifetree.application.dto.result.ResultDto
import com.lifetree.domain.model.result.Result
import java.time.format.DateTimeFormatter

object ResultMapper {
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    fun toDto(result: Result): ResultDto {
        return ResultDto(
            id = result.id.toString(),
            title = result.getTitle(),
            description = result.getDescription(),
            status = result.getStatus().name,
            relatedRequirementId = result.relatedRequirementId?.toString(),
            createdBy = result.createdBy.toString(),
            createdAt = result.createdAt.format(dateTimeFormatter),
            updatedAt = result.getUpdatedAt().format(dateTimeFormatter)
        )
    }
}