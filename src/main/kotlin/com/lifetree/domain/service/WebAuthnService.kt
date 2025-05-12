package com.lifetree.domain.service

import com.lifetree.domain.model.user.User
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.webauthn.WebAuthnCredential
import com.lifetree.domain.model.webauthn.WebAuthnCredentialId
import com.lifetree.domain.repository.UserRepository
import com.lifetree.domain.repository.WebAuthnCredentialRepository
import com.lifetree.infrastructure.security.webauthn.WebAuthnProvider

/**
 * WebAuthn服务接口
 */
interface WebAuthnService {
    /**
     * 为用户生成注册选项
     */
    suspend fun generateRegistrationOptions(userId: UserId, username: String, displayName: String, authenticatorType: String?): Map<String, Any>

    /**
     * 验证注册结果并保存凭据
     */
    suspend fun verifyRegistrationAndSaveCredential(userId: UserId, username: String, registrationData: Map<String, Any>): WebAuthnCredential?

    /**
     * 生成认证选项
     */
    suspend fun generateAuthenticationOptions(username: String? = null): Map<String, Any>

    /**
     * 验证认证结果
     */
    suspend fun verifyAuthentication(authenticationData: Map<String, Any>): User?

    /**
     * 移除用户的所有凭据
     */
    suspend fun removeAllCredentialsForUser(userId: UserId): Int
}

/**
 * WebAuthn服务实现
 */
class WebAuthnServiceImpl(
    private val userRepository: UserRepository,
    private val credentialRepository: WebAuthnCredentialRepository,
    private val webAuthnProvider: WebAuthnProvider
) : WebAuthnService {

    override suspend fun generateRegistrationOptions(userId: UserId, username: String, displayName: String, authenticatorType: String?): Map<String, Any> {
        // 获取用户已存在的凭据
        val existingCredentials = credentialRepository.findByUserId(userId)

        return webAuthnProvider.generateRegistrationOptions(
            userId = userId,
            username = username,
            displayName = displayName,
            excludeCredentials = existingCredentials.map { it.getCredentialId() },
            authenticatorType = authenticatorType
        )
    }

    override suspend fun verifyRegistrationAndSaveCredential(userId: UserId, username: String, registrationData: Map<String, Any>): WebAuthnCredential? {
        // 验证注册数据
        val credentialData = webAuthnProvider.verifyRegistration(registrationData)
            ?: return null

        // 创建并保存凭据
        val credential = WebAuthnCredential.create(
            id = WebAuthnCredentialId.generate(),
            userId = userId,
            name = username,
            credentialId = credentialData.credentialId,
            publicKey = credentialData.publicKey,
            counter = credentialData.counter,
            credentialFormat = credentialData.format
        )

        return credentialRepository.save(credential)
    }

    override suspend fun generateAuthenticationOptions(username: String?): Map<String, Any> {
        // 如果提供了用户名，则只允许该用户的凭据
        val allowCredentials = username?.let {
            val user = userRepository.findByEmail(username)
            user?.let {
                credentialRepository.findByUserId(user.id).map { it.getCredentialId() }
            }
        } ?: emptyList()

        return webAuthnProvider.generateAuthenticationOptions(allowCredentials)
    }

    override suspend fun verifyAuthentication(authenticationData: Map<String, Any>): User? {
        // 验证认证数据
        val authResult = webAuthnProvider.verifyAuthentication(authenticationData)
            ?: return null

        // 查找凭据
        val credential = credentialRepository.findByCredentialId(authResult.credentialId)
            ?: return null

        // 更新计数器
        if (authResult.counter > credential.getCounter()) {
            credential.updateCounter(authResult.counter)
            credentialRepository.save(credential)
        }

        // 返回对应的用户
        return userRepository.findById(credential.userId)
    }

    override suspend fun removeAllCredentialsForUser(userId: UserId): Int {
        return credentialRepository.deleteByUserId(userId)
    }
}