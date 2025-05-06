// 完整的需求控制器 (RequirementController.kt)

package com.lifetree.presentation.controller

import com.lifetree.application.dto.requirement.CreateRequirementDto
import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.RequirementWithApplicationDto
import com.lifetree.application.dto.requirement.UpdateRequirementDto
import com.lifetree.application.service.RequirementApplicationService
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.user.UserId
import io.ktor.server.auth.jwt.*

class RequirementController(
    private val requirementService: RequirementApplicationService
) {
    // 获取所有需求
    suspend fun getAllRequirements(): List<RequirementDto> {
        return requirementService.getAllRequirements()
    }

    // 获取需求详情
    suspend fun getRequirementById(id: String): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.getRequirementById(requirementId)
    }

    // 获取用户创建的需求
    suspend fun getRequirementsByUser(userId: String): List<RequirementDto> {
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return requirementService.getRequirementsByUser(userIdObj)
    }

    // 创建需求
    suspend fun createRequirement(createDto: CreateRequirementDto, principal: JWTPrincipal): RequirementDto? {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.createRequirement(createDto, userIdObj)
    }

    // 更新需求
    suspend fun updateRequirement(id: String, updateDto: UpdateRequirementDto): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.updateRequirement(requirementId, updateDto)
    }

    // 删除需求
    suspend fun deleteRequirement(id: String): Boolean {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return false
        }

        return requirementService.deleteRequirement(requirementId)
    }

    // 新增的方法

    // 获取当前用户创建的所有需求（包含申请信息）
    suspend fun getMyRequirementsWithApplications(principal: JWTPrincipal): List<RequirementWithApplicationDto> {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return requirementService.getRequirementsWithApplications(userIdObj)
    }

    // 获取当前用户申请的所有需求
    suspend fun getMyApplications(principal: JWTPrincipal): List<RequirementWithApplicationDto> {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return requirementService.getApplications(userIdObj)
    }

    // 申请接受需求
    suspend fun acceptRequirement(id: String, principal: JWTPrincipal): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.acceptRequirement(requirementId, userIdObj)
    }

    // 同意申请
    suspend fun approveApplication(id: String, principal: JWTPrincipal): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.approveApplication(requirementId, userIdObj)
    }

    // 拒绝申请
    suspend fun rejectApplication(id: String, principal: JWTPrincipal): RequirementDto? {
        val requirementId = try {
            RequirementId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return requirementService.rejectApplication(requirementId, userIdObj)
    }
}