// WebAuthnApplicationService.kt - WebAuthn应用服务
package com.lifetree.application.service

import com.lifetree.application.dto.webauthn.*
import com.lifetree.application.mapper.UserMapper
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.repository.UserRepository
import com.lifetree.domain.service.WebAuthnService
import com.lifetree.infrastructure.security.JwtProvider

/**
 * WebAuthn应用服务
 */
class WebAuthnApplicationService(
    private val webAuthnService: WebAuthnService,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider
) {
    /**
     * 生成注册选项
     */
    suspend fun generateRegistrationOptions(
        userId: String,
        request: RegistrationOptionsRequestDto
    ): RegistrationOptionsResponseDto? {
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val user = userRepository.findById(userIdObj) ?: return null

        // 从WebAuthn服务获取选项
        val options = webAuthnService.generateRegistrationOptions(
            userId = userIdObj,
            username = request.username,
            displayName = request.displayName,
            authenticatorType = request.authenticatorType
        )

        // 将Map转换为DTO
        @Suppress("UNCHECKED_CAST")
        return RegistrationOptionsResponseDto(
            challenge = options["challenge"] as String,
            rp = (options["rp"] as Map<String, String>).let {
                RelyingPartyDto(
                    id = it["id"] ?: "",
                    name = it["name"] ?: ""
                )
            },
            user = (options["user"] as Map<String, String>).let {
                UserDto(
                    id = it["id"] ?: "",
                    name = it["name"] ?: "",
                    displayName = it["displayName"] ?: ""
                )
            },
            pubKeyCredParams = (options["pubKeyCredParams"] as List<Map<String, Any>>).map {
                PubKeyCredParamDto(
                    type = it["type"] as String,
                    alg = (it["alg"] as Number).toInt()
                )
            },
            timeout = options["timeout"] as? Int ?: 60000,
            excludeCredentials = if (options.containsKey("excludeCredentials")) {
                (options["excludeCredentials"] as List<Map<String, Any>>).map {
                    CredentialDescriptorDto(
                        type = it["type"] as String,
                        id = it["id"] as String,
                        transports = it["transports"] as? List<String>
                    )
                }
            } else {
                emptyList()
            },
            authenticatorSelection = if (options.containsKey("authenticatorSelection")) {
                (options["authenticatorSelection"] as Map<String, Any>).let {
                    AuthenticatorSelectionDto(
                        authenticatorAttachment = it["authenticatorAttachment"] as? String,
                        residentKey = it["residentKey"] as? String,
                        requireResidentKey = it["requireResidentKey"] as? Boolean,
                        userVerification = it["userVerification"] as? String ?: "preferred"
                    )
                }
            } else {
                null
            },
            attestation = options["attestation"] as? String ?: "direct"
        )
    }

    /**
     * 验证注册
     */
    suspend fun verifyRegistration(
        userId: String,
        registrationResult: RegistrationResultDto
    ): RegistrationSuccessDto? {
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val user = userRepository.findById(userIdObj) ?: return null

        // 将DTO转换为Map
        val registrationData = mapOf(
            "id" to registrationResult.id,
            "rawId" to registrationResult.rawId,
            "type" to registrationResult.type,
            "response" to mapOf(
                "attestationObject" to registrationResult.response.attestationObject,
                "clientDataJSON" to registrationResult.response.clientDataJSON
            ),
            "clientExtensionResults" to registrationResult.clientExtensionResults
        )

        // 验证注册并保存凭据
        val credential = webAuthnService.verifyRegistrationAndSaveCredential(
            userId = userIdObj,
            username = user.getEmail(),
            registrationData = registrationData
        ) ?: return null

        return RegistrationSuccessDto(
            credentialId = credential.getCredentialId()
        )
    }

    /**
     * 生成认证选项
     */
    suspend fun generateAuthenticationOptions(
        request: AuthenticationOptionsRequestDto
    ): AuthenticationOptionsResponseDto {
        // 从WebAuthn服务获取选项
        val options = webAuthnService.generateAuthenticationOptions(
            username = request.username
        )

        // 将Map转换为DTO
        @Suppress("UNCHECKED_CAST")
        return AuthenticationOptionsResponseDto(
            challenge = options["challenge"] as String,
            timeout = options["timeout"] as? Int ?: 60000,
            rpId = options["rpId"] as String,
            allowCredentials = if (options.containsKey("allowCredentials")) {
                (options["allowCredentials"] as List<Map<String, Any>>).map {
                    CredentialDescriptorDto(
                        type = it["type"] as String,
                        id = it["id"] as String,
                        transports = it["transports"] as? List<String>
                    )
                }
            } else {
                emptyList()
            },
            userVerification = options["userVerification"] as? String ?: "preferred"
        )
    }

    /**
     * 验证认证并生成JWT
     */
    suspend fun verifyAuthentication(
        authenticationResult: AuthenticationResultDto
    ): AuthenticationSuccessDto? {
        // 将DTO转换为Map
        val authenticationData = mapOf(
            "id" to authenticationResult.id,
            "rawId" to authenticationResult.rawId,
            "type" to authenticationResult.type,
            "response" to mapOf(
                "authenticatorData" to authenticationResult.response.authenticatorData,
                "signature" to authenticationResult.response.signature,
                "clientDataJSON" to authenticationResult.response.clientDataJSON,
                "userHandle" to authenticationResult.response.userHandle
            ),
            "clientExtensionResults" to authenticationResult.clientExtensionResults
        )

        // 验证认证
        val user = webAuthnService.verifyAuthentication(authenticationData)
            ?: return null

        // 生成JWT令牌
        val token = jwtProvider.generateToken(user)

        // 返回成功响应
        return AuthenticationSuccessDto(
            user = UserMapper.toDto(user),
            token = token
        )
    }
}