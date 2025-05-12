// 需求应用服务更新 (RequirementApplicationService.kt) (添加对协议的支持)

package com.lifetree.application.service

import com.lifetree.application.dto.requirement.CreateRequirementDto
import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.RequirementWithApplicationDto
import com.lifetree.application.dto.requirement.UpdateRequirementDto
import com.lifetree.application.dto.requirement.application.ApplicationDto
import com.lifetree.application.mapper.RequirementMapper
import com.lifetree.domain.model.requirement.Requirement
import com.lifetree.domain.model.requirement.RequirementId
import com.lifetree.domain.model.requirement.RequirementStatus
import com.lifetree.domain.model.requirement.application.ApplicationStatus
import com.lifetree.domain.model.requirement.application.RequirementApplication
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.RequirementApplicationRepository
import com.lifetree.domain.repository.RequirementRepository
import com.lifetree.domain.repository.UserRepository

class RequirementApplicationService(
    private val requirementRepository: RequirementRepository,
    private val requirementApplicationRepository: RequirementApplicationRepository,
    private val userRepository: UserRepository
) {
    // 获取所有需求
    suspend fun getAllRequirements(): List<RequirementDto> {
        return requirementRepository.findAll()
            .map { RequirementMapper.toDto(it) }
    }

    // 获取需求详情
    suspend fun getRequirementById(id: RequirementId): RequirementDto? {
        return requirementRepository.findById(id)
            ?.let { RequirementMapper.toDto(it) }
    }

    // 获取用户创建的需求
    suspend fun getRequirementsByUser(userId: UserId): List<RequirementDto> {
        return requirementRepository.findByCreatedBy(userId)
            .map { RequirementMapper.toDto(it) }
    }

    // 创建需求 (添加对协议的支持)
    suspend fun createRequirement(dto: CreateRequirementDto, currentUserId: UserId): RequirementDto {
        val requirement = Requirement.create(
            id = RequirementId.generate(),
            title = dto.title,
            description = dto.description,
            agreement = dto.agreement, // 设置协议内容
            createdBy = currentUserId
        )

        val savedRequirement = requirementRepository.save(requirement)
        return RequirementMapper.toDto(savedRequirement)
    }

    // 更新需求 (添加对协议的支持)
    suspend fun updateRequirement(id: RequirementId, dto: UpdateRequirementDto): RequirementDto? {
        val requirement = requirementRepository.findById(id) ?: return null

        // 应用更新
        dto.title?.let { requirement.updateTitle(it) }
        dto.description?.let { requirement.updateDescription(it) }

        // 更新协议内容，注意这里可能是设置为null
        requirement.updateAgreement(dto.agreement)

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

    // 删除需求
    suspend fun deleteRequirement(id: RequirementId): Boolean {
        return requirementRepository.delete(id)
    }

    // 更新需求协议（单独API）
    suspend fun updateRequirementAgreement(id: RequirementId, agreement: String?): RequirementDto? {
        val requirement = requirementRepository.findById(id) ?: return null

        requirement.updateAgreement(agreement)
        val updatedRequirement = requirementRepository.save(requirement)
        return RequirementMapper.toDto(updatedRequirement)
    }

    // 以下保留原有方法，略去代码内容...

    // 获取指定用户创建的所有需求，包含所有申请信息
    suspend fun getRequirementsWithApplications(userId: UserId): List<RequirementWithApplicationDto> {
        // 原有实现保持不变
        val requirements = requirementRepository.findByCreatedBy(userId)
        val result = mutableListOf<RequirementWithApplicationDto>()

        for (requirement in requirements) {
            // 获取该需求的所有申请
            val applications = requirementApplicationRepository.findByRequirementId(requirement.id)

            if (applications.isNotEmpty()) {
                // 获取所有待处理的申请
                val pendingApplications = applications.filter { it.getStatus() == ApplicationStatus.PENDING }

                if (pendingApplications.isNotEmpty()) {
                    // 有待处理的申请，为每个申请创建一个DTO
                    for (application in pendingApplications) {
                        val applicant = userRepository.findById(application.getApplicantId())
                        val dto = RequirementMapper.toWithApplicationDto(
                            requirement,
                            application,
                            applicant,
                            true,
                            pendingApplications.size
                        )
                        result.add(dto)
                    }
                } else {
                    // 没有待处理的申请，创建一个无申请信息的DTO
                    val dto = RequirementMapper.toWithApplicationDto(
                        requirement,
                        null,
                        null,
                        false,
                        0
                    )
                    result.add(dto)
                }
            } else {
                // 没有申请，创建一个无申请信息的DTO
                val dto = RequirementMapper.toWithApplicationDto(
                    requirement,
                    null,
                    null,
                    false,
                    0
                )
                result.add(dto)
            }
        }

        return result
    }

    // 获取当前用户申请的需求
    suspend fun getApplications(userId: UserId): List<RequirementWithApplicationDto> {
        // 原有实现保持不变
        val applications = requirementApplicationRepository.findByApplicantId(userId)
        val result = mutableListOf<RequirementWithApplicationDto>()

        for (application in applications) {
            val requirement = requirementRepository.findById(application.getRequirementId())
            if (requirement != null) {
                val owner = userRepository.findById(requirement.createdBy)
                val dto = RequirementMapper.toWithApplicationDto(
                    requirement,
                    application,
                    owner,
                    false,
                    0
                )
                result.add(dto)
            }
        }

        return result
    }

    // 申请接受需求的方法、同意申请方法、拒绝申请方法等保持不变...

    // 申请接受需求方法 - 允许多人申请同一个需求
    suspend fun acceptRequirement(requirementId: RequirementId, applicantId: UserId): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是自己的需求
        if (requirement.createdBy == applicantId) {
            throw IllegalArgumentException("不能申请自己的需求")
        }

        // 检查需求状态是否为CREATED或IN_PROGRESS (可接受的状态)
        if (requirement.getStatus() != RequirementStatus.CREATED &&
            requirement.getStatus() != RequirementStatus.IN_PROGRESS
        ) {
            throw IllegalArgumentException("该需求当前状态不可申请接受")
        }

        // 检查用户是否已经申请过这个需求
        val existingApplications = requirementApplicationRepository.findByRequirementId(requirementId)
        val userApplication = existingApplications.find {
            it.getApplicantId() == applicantId &&
                    (it.getStatus() == ApplicationStatus.PENDING || it.getStatus() == ApplicationStatus.APPROVED)
        }

        if (userApplication != null) {
            throw IllegalArgumentException("您已经申请过这个需求")
        }

        // 创建申请记录
        val application = RequirementApplication.create(
            requirementId = requirementId,
            applicantId = applicantId
        )
        requirementApplicationRepository.save(application)

        // 返回最新的需求信息
        return RequirementMapper.toDto(requirement)
    }

    // 同意申请方法保持不变
    suspend fun approveApplication(
        requirementId: RequirementId,
        ownerId: UserId,
        applicationId: String
    ): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是需求创建者
        if (requirement.createdBy != ownerId) {
            throw IllegalArgumentException("只有需求创建者可以审批申请")
        }

        // 获取指定的申请记录
        val applicationIdObj = try {
            com.lifetree.domain.model.requirement.application.ApplicationId.fromString(applicationId)
        } catch (e: Exception) {
            throw IllegalArgumentException("无效的申请ID")
        }

        val application = requirementApplicationRepository.findById(applicationIdObj)
            ?: throw IllegalArgumentException("未找到指定的申请")

        // 检查申请状态
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw IllegalArgumentException("该申请已被处理")
        }

        // 更新申请状态为已同意
        application.approve()
        requirementApplicationRepository.save(application)

        // 如果需求状态是CREATED，则更新为IN_PROGRESS
        if (requirement.getStatus() == RequirementStatus.CREATED) {
            requirement.updateStatus(RequirementStatus.IN_PROGRESS)
            requirementRepository.save(requirement)
        }

        // 返回更新后的需求
        return RequirementMapper.toDto(requirement)
    }

    // 拒绝申请方法保持不变
    suspend fun rejectApplication(
        requirementId: RequirementId,
        ownerId: UserId,
        applicationId: String
    ): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是需求创建者
        if (requirement.createdBy != ownerId) {
            throw IllegalArgumentException("只有需求创建者可以审批申请")
        }

        // 获取指定的申请记录
        val applicationIdObj = try {
            com.lifetree.domain.model.requirement.application.ApplicationId.fromString(applicationId)
        } catch (e: Exception) {
            throw IllegalArgumentException("无效的申请ID")
        }

        val application = requirementApplicationRepository.findById(applicationIdObj)
            ?: throw IllegalArgumentException("未找到指定的申请")

        // 检查申请状态
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw IllegalArgumentException("该申请已被处理")
        }

        // 更新申请状态为已拒绝
        application.reject()
        requirementApplicationRepository.save(application)

        // 需求状态保持不变
        return RequirementMapper.toDto(requirement)
    }

    // 获取指定需求的所有申请方法保持不变
    suspend fun getApplicationsByRequirement(requirementId: RequirementId): List<ApplicationDto> {
        val applications = requirementApplicationRepository.findByRequirementId(requirementId)

        return applications.map { application ->
            val applicant = userRepository.findById(application.getApplicantId())
            ApplicationDto(
                id = application.id.toString(),
                requirementId = application.getRequirementId().toString(),
                applicantId = application.getApplicantId().toString(),
                applicantName = applicant?.getName() ?: "未知用户",
                applicantEmail = applicant?.getEmail() ?: "",
                status = application.getStatus().name,
                createdAt = application.createdAt.toString(),
                updatedAt = application.getUpdatedAt().toString()
            )
        }
    }
}