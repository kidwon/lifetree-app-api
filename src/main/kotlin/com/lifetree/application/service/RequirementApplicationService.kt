// RequirementApplicationService.kt - 需求应用服务
package com.lifetree.application.service

import com.lifetree.application.dto.requirement.CreateRequirementDto
import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.UpdateRequirementDto
import com.lifetree.application.mapper.RequirementMapper
import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.RequirementStatus
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.RequirementRepository
import java.util.UUID

class RequirementApplicationService(
    private val requirementRepository: RequirementRepository
) {
    suspend fun getAllRequirements(): List<RequirementDto> {
        return requirementRepository.findAll()
            .map { RequirementMapper.toDto(it) }
    }

    suspend fun getRequirementById(id: RequirementId): RequirementDto? {
        return requirementRepository.findById(id)
            ?.let { RequirementMapper.toDto(it) }
    }

    suspend fun getRequirementsByUser(userId: UserId): List<RequirementDto> {
        return requirementRepository.findByCreatedBy(userId)
            .map { RequirementMapper.toDto(it) }
    }

    suspend fun createRequirement(dto: CreateRequirementDto): RequirementDto {
        // TODO: 从认证上下文中获取当前用户ID
        val userId = UserId(UUID.randomUUID()) // 临时，应该从JWT获取

        val requirement = Requirement.create(
            id = RequirementId.generate(),
            title = dto.title,
            description = dto.description,
            createdBy = userId
        )

        val savedRequirement = requirementRepository.save(requirement)
        return RequirementMapper.toDto(savedRequirement)
    }

    suspend fun updateRequirement(id: RequirementId, dto: UpdateRequirementDto): RequirementDto? {
        val requirement = requirementRepository.findById(id) ?: return null

        // 应用更新
        dto.title?.let { requirement.updateTitle(it) }
        dto.description?.let { requirement.updateDescription(it) }
        dto.status?.let {
            try {
                val status = RequirementStatus.fromString(it)
                requirement.updateStatus(status)
            } catch (e: IllegalArgumentException) {
                // 忽略无效的状态值
            }
        }

        val updatedRequirement = requirementRepository.save(requirement)
        return RequirementMapper.toDto(updatedRequirement)
    }

    suspend fun deleteRequirement(id: RequirementId): Boolean {
        return requirementRepository.delete(id)
    }
}