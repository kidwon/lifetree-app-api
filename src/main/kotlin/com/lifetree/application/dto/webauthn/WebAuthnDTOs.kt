// WebAuthnDTOs.kt - WebAuthn数据传输对象
package com.lifetree.application.dto.webauthn

import kotlinx.serialization.Serializable

/**
 * 注册时请求创建凭据选项的DTO
 */
@Serializable
data class RegistrationOptionsRequestDto(
    val username: String,
    val displayName: String,
    val authenticatorType: String = "any" // platform, cross-platform, or any
)

/**
 * 注册时返回给客户端的凭据创建选项
 */
@Serializable
data class RegistrationOptionsResponseDto(
    val challenge: String,
    val rp: RelyingPartyDto,
    val user: UserDto,
    val pubKeyCredParams: List<PubKeyCredParamDto>,
    val timeout: Int = 60000,
    val excludeCredentials: List<CredentialDescriptorDto> = emptyList(),
    val authenticatorSelection: AuthenticatorSelectionDto? = null,
    val attestation: String = "direct"
)

/**
 * 认证时请求断言选项的DTO
 */
@Serializable
data class AuthenticationOptionsRequestDto(
    val username: String? = null
)

/**
 * 认证时返回给客户端的断言请求选项
 */
@Serializable
data class AuthenticationOptionsResponseDto(
    val challenge: String,
    val timeout: Int = 60000,
    val rpId: String,
    val allowCredentials: List<CredentialDescriptorDto> = emptyList(),
    val userVerification: String = "preferred"
)

/**
 * 注册结果提交DTO
 */
@Serializable
data class RegistrationResultDto(
    val id: String,
    val rawId: String,
    val type: String,
    val response: RegistrationResponseDto,
    val clientExtensionResults: Map<String, String> = emptyMap()
)

/**
 * 注册响应数据
 */
@Serializable
data class RegistrationResponseDto(
    val attestationObject: String,
    val clientDataJSON: String
)

/**
 * 认证结果提交DTO
 */
@Serializable
data class AuthenticationResultDto(
    val id: String,
    val rawId: String,
    val type: String,
    val response: AuthenticationResponseDto,
    val clientExtensionResults: Map<String, String> = emptyMap()
)

/**
 * 认证响应数据
 */
@Serializable
data class AuthenticationResponseDto(
    val authenticatorData: String,
    val signature: String,
    val clientDataJSON: String,
    val userHandle: String? = null
)

// 辅助DTO类
@Serializable
data class RelyingPartyDto(
    val id: String,
    val name: String
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val displayName: String
)

@Serializable
data class PubKeyCredParamDto(
    val type: String = "public-key",
    val alg: Int
)

@Serializable
data class CredentialDescriptorDto(
    val type: String = "public-key",
    val id: String,
    val transports: List<String>? = null
)

@Serializable
data class AuthenticatorSelectionDto(
    val authenticatorAttachment: String? = null,
    val residentKey: String? = null,
    val requireResidentKey: Boolean? = null,
    val userVerification: String = "preferred"
)

/**
 * WebAuthn注册成功响应
 */
@Serializable
data class RegistrationSuccessDto(
    val status: String = "success",
    val credentialId: String
)

/**
 * WebAuthn登录成功响应
 */
@Serializable
data class AuthenticationSuccessDto(
    val status: String = "success",
    val user: com.lifetree.application.dto.user.UserDto,
    val token: String
)

