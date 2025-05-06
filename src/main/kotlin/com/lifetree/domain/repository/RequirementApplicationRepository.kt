// 需求申请仓储接口 (RequirementApplicationRepository.kt)
package com.lifetree.domain.repository

import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.application.ApplicationId
import com.lifetree.domain.model.requirement.application.RequirementApplication
import com.lifetree.domain.model.user.UserId

interface RequirementApplicationRepository {
    suspend fun findById(id: ApplicationId): RequirementApplication?
    suspend fun findByRequirementId(requirementId: RequirementId): List<RequirementApplication>
    suspend fun findByApplicantId(applicantId: UserId): List<RequirementApplication>
    suspend fun save(application: RequirementApplication): RequirementApplication
    suspend fun delete(id: ApplicationId): Boolean
}


