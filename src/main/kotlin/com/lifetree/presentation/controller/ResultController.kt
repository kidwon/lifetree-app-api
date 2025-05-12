package com.lifetree.presentation.controller

import com.lifetree.application.dto.result.CreateResultDto
import com.lifetree.application.dto.result.ResultDto
import com.lifetree.application.dto.result.UpdateResultDto
import com.lifetree.application.service.ResultApplicationService
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.result.ResultId
import com.lifetree.domain.model.user.UserId
import io.ktor.server.auth.jwt.*

class ResultController(
    private val resultService: ResultApplicationService
) {
    suspend fun getAllResults(): List<ResultDto> {
        return resultService.getAllResults()
    }

    suspend fun getResultById(id: String): ResultDto? {
        val resultId = try {
            ResultId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return resultService.getResultById(resultId)
    }

    suspend fun getResultsByUser(userId: String): List<ResultDto> {
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return resultService.getResultsByUser(userIdObj)
    }

    suspend fun getResultsByRequirement(requirementId: String): List<ResultDto> {
        val requirementIdObj = try {
            RequirementId.fromString(requirementId)
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }

        return resultService.getResultsByRequirement(requirementIdObj)
    }

    suspend fun createResult(createDto: CreateResultDto, principal: JWTPrincipal): ResultDto? {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return resultService.createResult(createDto, userIdObj)
    }

    suspend fun updateResult(id: String, updateDto: UpdateResultDto): ResultDto? {
        val resultId = try {
            ResultId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return resultService.updateResult(resultId, updateDto)
    }

    suspend fun deleteResult(id: String): Boolean {
        val resultId = try {
            ResultId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return false
        }

        return resultService.deleteResult(resultId)
    }
}