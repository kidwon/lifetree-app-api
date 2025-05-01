package com.lifetree.domain.service

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.Result
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.result.ResultStatus
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.ResultRepository

/**
 * 结果领域服务
 * 处理跨聚合根或复杂的领域逻辑
 */
class ResultService(
    private val resultRepository: ResultRepository
) {
    /**
     * 更改结果状态
     */
    suspend fun changeStatus(resultId: ResultId, newStatus: ResultStatus): Result? {
        val result = resultRepository.findById(resultId) ?: return null

        // 可以在这里添加状态转换的业务规则

        result.updateStatus(newStatus)
        return resultRepository.save(result)
    }

    /**
     * 检查用户是否可以编辑结果
     */
    suspend fun canEditResult(resultId: ResultId, userId: UserId): Boolean {
        val result = resultRepository.findById(resultId) ?: return false

        // 简单的规则：创建者可以编辑
        return result.createdBy == userId
    }

    /**
     * 查找与特定需求相关的所有结果
     */
    suspend fun findResultsByRequirement(requirementId: RequirementId): List<Result> {
        return resultRepository.findByRequirementId(requirementId)
    }

    /**
     * 查找用户创建的所有结果
     */
    suspend fun findResultsByUser(userId: UserId): List<Result> {
        return resultRepository.findByCreatedBy(userId)
    }

    /**
     * 根据复杂条件查找结果
     */
    suspend fun findResultsWithCriteria(
        status: ResultStatus? = null,
        createdBy: UserId? = null,
        relatedRequirementId: RequirementId? = null,
        olderThanDays: Int? = null
    ): List<Result> {
        // 在真实应用中，可能会通过仓储层的特定查询方法实现
        // 这里为了演示，我们获取所有结果并在内存中筛选
        val allResults = resultRepository.findAll()

        return allResults.filter { result ->
            var matches = true

            if (status != null && result.getStatus() != status) {
                matches = false
            }

            if (createdBy != null && result.createdBy != createdBy) {
                matches = false
            }

            if (relatedRequirementId != null && result.relatedRequirementId != relatedRequirementId) {
                matches = false
            }

            if (olderThanDays != null) {
                val olderThanDate = java.time.LocalDateTime.now().minusDays(olderThanDays.toLong())
                if (result.createdAt.isAfter(olderThanDate)) {
                    matches = false
                }
            }

            matches
        }
    }

    /**
     * 批量归档结果
     */
    suspend fun archiveResults(resultIds: List<ResultId>): Int {
        var archivedCount = 0

        for (resultId in resultIds) {
            val result = resultRepository.findById(resultId) ?: continue

            // 假设ARCHIVED是ResultStatus的一个可能值
            result.updateStatus(ResultStatus.ARCHIVED)
            resultRepository.save(result)
            archivedCount++
        }

        return archivedCount
    }
}