// 完整的需求应用服务 (RequirementApplicationService.kt)

package com.lifetree.application.service

import com.lifetree.application.dto.requirement.CreateRequirementDto
import com.lifetree.application.dto.requirement.RequirementDto
import com.lifetree.application.dto.requirement.RequirementWithApplicationDto
import com.lifetree.application.dto.requirement.UpdateRequirementDto
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
    // 原有的方法

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

    // 创建需求
    suspend fun createRequirement(dto: CreateRequirementDto, currentUserId: UserId): RequirementDto {
        val requirement = Requirement.create(
            id = RequirementId.generate(),
            title = dto.title,
            description = dto.description,
            createdBy = currentUserId
        )

        val savedRequirement = requirementRepository.save(requirement)
        return RequirementMapper.toDto(savedRequirement)
    }

    // 更新需求
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

    // 删除需求
    suspend fun deleteRequirement(id: RequirementId): Boolean {
        return requirementRepository.delete(id)
    }

    // 新增的方法

    // 获取指定用户创建的所有需求，包含申请信息
    suspend fun getRequirementsWithApplications(userId: UserId): List<RequirementWithApplicationDto> {
        val requirements = requirementRepository.findByCreatedBy(userId)
        val result = mutableListOf<RequirementWithApplicationDto>()

        for (requirement in requirements) {
            val applications = requirementApplicationRepository.findByRequirementId(requirement.id)
            val dto = if (applications.isNotEmpty()) {
                // 找出状态为等待确认的申请
                val pendingApplication = applications.find { it.getStatus() == ApplicationStatus.PENDING }

                if (pendingApplication != null) {
                    val applicant = userRepository.findById(pendingApplication.getApplicantId())
                    RequirementMapper.toWithApplicationDto(
                        requirement,
                        pendingApplication,
                        applicant,
                        true
                    )
                } else {
                    RequirementMapper.toWithApplicationDto(requirement, null, null, false)
                }
            } else {
                RequirementMapper.toWithApplicationDto(requirement, null, null, false)
            }

            result.add(dto)
        }

        return result
    }

    // 获取当前用户申请的需求
    suspend fun getApplications(userId: UserId): List<RequirementWithApplicationDto> {
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
                    false
                )
                result.add(dto)
            }
        }

        return result
    }

    // 申请接受需求
    suspend fun acceptRequirement(requirementId: RequirementId, applicantId: UserId): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是自己的需求
        if (requirement.createdBy == applicantId) {
            throw IllegalArgumentException("不能申请自己的需求")
        }

        // 检查需求状态
        if (requirement.getStatus() != RequirementStatus.CREATED) {
            throw IllegalArgumentException("该需求当前状态不可申请接受")
        }

        // 检查是否已经有未处理的申请
        val existingApplications = requirementApplicationRepository.findByRequirementId(requirementId)
        val pendingApplication = existingApplications.find { it.getStatus() == ApplicationStatus.PENDING }
        if (pendingApplication != null) {
            throw IllegalArgumentException("该需求已有人申请，等待确认中")
        }

        // 创建申请记录
        val application = RequirementApplication.create(
            requirementId = requirementId,
            applicantId = applicantId
        )
        requirementApplicationRepository.save(application)

        // 更新需求状态为确认中
        requirement.updateStatus(RequirementStatus.CONFIRMING)
        val updatedRequirement = requirementRepository.save(requirement)

        return RequirementMapper.toDto(updatedRequirement)
    }

    // 同意申请
    suspend fun approveApplication(requirementId: RequirementId, ownerId: UserId): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是需求创建者
        if (requirement.createdBy != ownerId) {
            throw IllegalArgumentException("只有需求创建者可以审批申请")
        }

        // 检查需求状态
        if (requirement.getStatus() != RequirementStatus.CONFIRMING) {
            throw IllegalArgumentException("该需求当前状态不是确认中")
        }

        // 获取申请记录
        val applications = requirementApplicationRepository.findByRequirementId(requirementId)
        val pendingApplication = applications.find { it.getStatus() == ApplicationStatus.PENDING }
            ?: throw IllegalArgumentException("未找到待处理的申请")

        // 更新申请状态为已同意
        pendingApplication.approve()
        requirementApplicationRepository.save(pendingApplication)

        // 更新需求状态为进行中
        requirement.updateStatus(RequirementStatus.IN_PROGRESS)
        val updatedRequirement = requirementRepository.save(requirement)

        return RequirementMapper.toDto(updatedRequirement)
    }

    // 拒绝申请
    suspend fun rejectApplication(requirementId: RequirementId, ownerId: UserId): RequirementDto? {
        val requirement = requirementRepository.findById(requirementId) ?: return null

        // 检查是否是需求创建者
        if (requirement.createdBy != ownerId) {
            throw IllegalArgumentException("只有需求创建者可以审批申请")
        }

        // 检查需求状态
        if (requirement.getStatus() != RequirementStatus.CONFIRMING) {
            throw IllegalArgumentException("该需求当前状态不是确认中")
        }

        // 获取申请记录
        val applications = requirementApplicationRepository.findByRequirementId(requirementId)
        val pendingApplication = applications.find { it.getStatus() == ApplicationStatus.PENDING }
            ?: throw IllegalArgumentException("未找到待处理的申请")

        // 更新申请状态为已拒绝
        pendingApplication.reject()
        requirementApplicationRepository.save(pendingApplication)

        // 更新需求状态为已创建
        requirement.updateStatus(RequirementStatus.CREATED)
        val updatedRequirement = requirementRepository.save(requirement)

        return RequirementMapper.toDto(updatedRequirement)
    }
}