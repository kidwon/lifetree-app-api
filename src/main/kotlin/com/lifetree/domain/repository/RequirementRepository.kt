// RequirementRepository.kt - 需求仓储接口
package com.lifetree.domain.repository

import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.user.UserId

interface RequirementRepository {
    suspend fun findById(id: RequirementId): Requirement?
    suspend fun findAll(): List<Requirement>
    suspend fun findByCreatedBy(userId: UserId): List<Requirement>
    suspend fun save(requirement: Requirement): Requirement
    suspend fun delete(id: RequirementId): Boolean
}





