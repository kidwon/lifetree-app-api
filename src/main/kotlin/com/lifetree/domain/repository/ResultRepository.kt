package com.lifetree.domain.repository

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.Result
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.user.UserId

interface ResultRepository {
    suspend fun findById(id: ResultId): Result?
    suspend fun findAll(): List<Result>
    suspend fun findByCreatedBy(userId: UserId): List<Result>
    suspend fun findByRequirementId(requirementId: RequirementId): List<Result>
    suspend fun save(result: Result): Result
    suspend fun delete(id: ResultId): Boolean
}