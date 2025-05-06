package com.lifetree.application.service

import com.lifetree.application.dto.result.CreateResultDto
import com.lifetree.application.dto.result.ResultDto
import com.lifetree.application.dto.result.UpdateResultDto
import com.lifetree.application.mapper.ResultMapper
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.Result
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.result.ResultStatus
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.ResultRepository

class ResultApplicationService(
    private val resultRepository: ResultRepository
) {
    suspend fun getAllResults(): List<ResultDto> {
        return resultRepository.findAll()
            .map { ResultMapper.toDto(it) }
    }

    suspend fun getResultById(id: ResultId): ResultDto? {
        return resultRepository.findById(id)
            ?.let { ResultMapper.toDto(it) }
    }

    suspend fun getResultsByUser(userId: UserId): List<ResultDto> {
        return resultRepository.findByCreatedBy(userId)
            .map { ResultMapper.toDto(it) }
    }

    suspend fun getResultsByRequirement(requirementId: RequirementId): List<ResultDto> {
        return resultRepository.findByRequirementId(requirementId)
            .map { ResultMapper.toDto(it) }
    }

    suspend fun createResult(dto: CreateResultDto, currentUserId: UserId): ResultDto {
        val requirementId = dto.relatedRequirementId?.let {
            RequirementId.fromString(it)
        }

        val result = Result.create(
            id = ResultId.generate(),
            title = dto.title,
            description = dto.description,
            relatedRequirementId = requirementId,
            createdBy = currentUserId
        )

        val savedResult = resultRepository.save(result)
        return ResultMapper.toDto(savedResult)
    }

    suspend fun updateResult(id: ResultId, dto: UpdateResultDto): ResultDto? {
        val result = resultRepository.findById(id) ?: return null

        // 应用更新
        dto.title?.let { result.updateTitle(it) }
        dto.description?.let { result.updateDescription(it) }
        dto.status?.let {
            try {
                val status = ResultStatus.fromString(it)
                result.updateStatus(status)
            } catch (e: IllegalArgumentException) {
                // 忽略无效的状态值
            }
        }

        val updatedResult = resultRepository.save(result)
        return ResultMapper.toDto(updatedResult)
    }

    suspend fun deleteResult(id: ResultId): Boolean {
        return resultRepository.delete(id)
    }
}