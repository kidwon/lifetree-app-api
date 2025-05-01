package com.lifetree.domain.service

import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.RequirementStatus
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.RequirementRepository

/**
 * 需求领域服务
 * 处理跨聚合根或复杂的领域逻辑
 */
class RequirementService(
    private val requirementRepository: RequirementRepository
) {
    /**
     * 更改需求状态
     */
    suspend fun changeStatus(requirementId: RequirementId, newStatus: RequirementStatus): Requirement? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 可以在这里添加状态转换的业务规则，例如:
        // 1. 某些状态不能直接转换到其他状态
        // 2. 特定角色才能执行某些状态转换
        // 3. 状态转换可能需要满足特定条件

        requirement.updateStatus(newStatus)
        return requirementRepository.save(requirement)
    }

    /**
     * 检查用户是否可以编辑需求
     */
    suspend fun canEditRequirement(requirementId: RequirementId, userId: UserId): Boolean {
        val requirement = requirementRepository.findById(requirementId) ?: return false

        // 简单的规则：创建者可以编辑
        // 在真实系统中，这里可能有更复杂的权限检查逻辑
        return requirement.createdBy == userId
    }

    /**
     * 根据复杂条件查找需求
     * 这是一个示例，展示领域服务如何处理复杂业务逻辑
     */
    suspend fun findRequirementsWithCriteria(
        status: RequirementStatus? = null,
        createdBy: UserId? = null,
        olderThanDays: Int? = null
    ): List<Requirement> {
        // 在真实应用中，可能会通过仓储层的特定查询方法实现
        // 这里为了演示，我们获取所有需求并在内存中筛选
        val allRequirements = requirementRepository.findAll()

        return allRequirements.filter { requirement ->
            var matches = true

            if (status != null && requirement.getStatus() != status) {
                matches = false
            }

            if (createdBy != null && requirement.createdBy != createdBy) {
                matches = false
            }

            if (olderThanDays != null) {
                val olderThanDate = java.time.LocalDateTime.now().minusDays(olderThanDays.toLong())
                if (requirement.createdAt.isAfter(olderThanDate)) {
                    matches = false
                }
            }

            matches
        }
    }

    /**
     * 将结果关联到需求
     * 示例：处理跨聚合根的操作
     */
    suspend fun associateResultWithRequirement(requirementId: RequirementId, resultId: ResultId): Boolean {
        // 注意：在真实系统中，这种关联可能需要特定的关联实体或值对象
        // 这里仅作为示例，展示领域服务如何协调多个聚合根

        val requirement = requirementRepository.findById(requirementId) ?: return false

        // 假设我们在需求中维护了一个关联结果的列表（实际实现可能有所不同）
        // requirement.addAssociatedResult(resultId)

        return true
    }
}