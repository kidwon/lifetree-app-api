// RequirementController.kt - 需求控制器
package com.lifetree.presentation.controller

import com.lifetree.application.dto.requirement.CreateRequirementDto
import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.UpdateRequirementDto
import com.lifetree.application.service.RequirementApplicationService
import com.lifetree.domain.model.requirement.RequirementId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class RequirementController(
    private val requirementService: RequirementApplicationService
) {
    suspend fun getAllRequirements(): List<RequirementDto> {
        return requirementService.getAllRequirements()
    }

    suspend fun getRequirementById(id: String): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.getRequirementById(requirementId)
    }

    suspend fun createRequirement(createDto: CreateRequirementDto): RequirementDto {
        return requirementService.createRequirement(createDto)
    }

    suspend fun updateRequirement(id: String, updateDto: UpdateRequirementDto): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.updateRequirement(requirementId, updateDto)
    }

    suspend fun deleteRequirement(id: String): Boolean {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return false
        }

        return requirementService.deleteRequirement(requirementId)
    }
}