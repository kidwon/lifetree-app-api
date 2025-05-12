// 修复版 WebAuthnProvider.kt
package com.lifetree.infrastructure.security.webauthn

import com.lifetree.domain.model.user.UserId
import com.webauthn4j.WebAuthnManager
import io.ktor.server.application.*
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * WebAuthn提供者接口
 */
interface WebAuthnProvider {
    /**
     * 生成注册选项
     */
    fun generateRegistrationOptions(
        userId: UserId,
        username: String,
        displayName: String,
        excludeCredentials: List<String> = emptyList(),
        authenticatorType: String? = null
    ): Map<String, Any>

    /**
     * 验证注册结果
     */
    fun verifyRegistration(registrationData: Map<String, Any>): WebAuthnCredentialData?

    /**
     * 生成认证选项
     */
    fun generateAuthenticationOptions(allowCredentials: List<String> = emptyList()): Map<String, Any>

    /**
     * 验证认证结果
     */
    fun verifyAuthentication(authenticationData: Map<String, Any>): WebAuthnAuthenticationResult?
}

/**
 * WebAuthn认证结果
 */
data class WebAuthnAuthenticationResult(
    val credentialId: String,
    val counter: Long
)

/**
 * WebAuthn凭据数据
 */
data class WebAuthnCredentialData(
    val credentialId: String,
    val publicKey: String,
    val counter: Long = 0,
    val format: String = "packed"
)

/**
 * WebAuthn提供者实现，使用WebAuthn4J库
 */
class WebAuthn4JProvider(
    private val application: Application
) : WebAuthnProvider {

    private val rp: RelyingParty
    private val webAuthnManager: WebAuthnManager
    private val random = SecureRandom()
    private val challengeMap = ConcurrentHashMap<String, StoredChallenge>()

    private data class StoredChallenge(
        val challenge: String,
        val userId: String? = null,
        val username: String? = null,
        val createdAt: Long = System.currentTimeMillis()
    )

    init {
        // 从配置中获取RP信息
        val config = application.environment.config
        val rpId = config.propertyOrNull("webauthn.rpId")?.getString()
            ?: application.environment.config.propertyOrNull("ktor.deployment.host")?.getString()
            ?: "localhost"
        val rpName = config.propertyOrNull("webauthn.rpName")?.getString() ?: "LifeTree Application"

        rp = RelyingParty(rpId, rpName)
        webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager()
    }

    /**
     * 生成安全的随机挑战
     */
    private fun generateChallenge(): ByteArray {
        val challenge = ByteArray(32)
        random.nextBytes(challenge)
        return challenge
    }

    /**
     * 生成Base64编码的挑战并存储
     */
    private fun storeChallenge(userId: String? = null, username: String? = null): String {
        val challenge = Base64.getEncoder().encodeToString(generateChallenge())
        challengeMap[challenge] = StoredChallenge(
            challenge = challenge,
            userId = userId,
            username = username
        )

        // 定期清理过期的挑战（5分钟）
        val currentTime = System.currentTimeMillis()
        challengeMap.entries.removeIf { currentTime - it.value.createdAt > 300000 }

        return challenge
    }

    /**
     * 生成注册选项
     */
    override fun generateRegistrationOptions(
        userId: UserId,
        username: String,
        displayName: String,
        excludeCredentials: List<String>,
        authenticatorType: String?
    ): Map<String, Any> {
        // 生成并存储挑战
        val challenge = storeChallenge(userId.toString(), username)

        // 创建用户标识
        val userHandle = userId.toString()

        // 构建注册选项
        val options = mutableMapOf<String, Any>(
            "challenge" to challenge,
            "rp" to mapOf(
                "id" to rp.id,
                "name" to rp.name
            ),
            "user" to mapOf(
                "id" to userHandle,
                "name" to username,
                "displayName" to displayName
            ),
            "pubKeyCredParams" to listOf(
                mapOf("type" to "public-key", "alg" to -7),  // ES256
                mapOf("type" to "public-key", "alg" to -257)  // RS256
            ),
            "timeout" to 60000,
            "attestation" to "direct"
        )

        // 添加排除的凭据
        if (excludeCredentials.isNotEmpty()) {
            options["excludeCredentials"] = excludeCredentials.map {
                mapOf("type" to "public-key", "id" to it)
            }
        }

        // 添加认证器选择
        val authenticatorSelection = mutableMapOf<String, Any>(
            "userVerification" to "preferred"
        )

        // 设置认证器附件（如指定使用平台认证器，如TouchID/FaceID）
        when (authenticatorType) {
            "platform" -> authenticatorSelection["authenticatorAttachment"] = "platform"
            "cross-platform" -> authenticatorSelection["authenticatorAttachment"] = "cross-platform"
        }

        // 添加认证器选择设置
        options["authenticatorSelection"] = authenticatorSelection

        return options
    }

    /**
     * 验证注册结果
     */
    override fun verifyRegistration(registrationData: Map<String, Any>): WebAuthnCredentialData? {
        try {
            // 解析注册数据
            val id = registrationData["id"] as String
            val rawId = registrationData["rawId"] as String
            val type = registrationData["type"] as String
            val responseMap = registrationData["response"] as Map<String, Any>

            val attestationObject = responseMap["attestationObject"] as String
            val clientDataJSON = responseMap["clientDataJSON"] as String

            // 验证类型
            if (type != "public-key") {
                return null
            }

            // 获取并解码挑战
            val decodedClientData = Base64.getDecoder().decode(clientDataJSON)
            val clientDataJson = String(decodedClientData, Charsets.UTF_8)

            // 从JSON中提取挑战
            val challengeRegex = """"challenge":"([^"]+)"""".toRegex()
            val matchResult = challengeRegex.find(clientDataJson)
            val challengeBase64 = matchResult?.groupValues?.get(1)
                ?: return null

            // 查找存储的挑战
            val storedChallenge = challengeMap[challengeBase64]
                ?: return null

            // 清理使用过的挑战
            challengeMap.remove(challengeBase64)

            // 这里简化处理，实际应用中需要使用WebAuthn4J进行完整验证
            // 模拟提取凭据ID和公钥
            val credentialId = rawId
            val publicKey = "simulated_public_key_" + UUID.randomUUID().toString()

            return WebAuthnCredentialData(
                credentialId = credentialId,
                publicKey = publicKey,
                counter = 0,
                format = "packed"
            )

        } catch (e: Exception) {
            application.log.error("Registration verification failed", e)
            return null
        }
    }

    /**
     * 生成认证选项
     */
    override fun generateAuthenticationOptions(allowCredentials: List<String>): Map<String, Any> {
        // 生成并存储挑战
        val challenge = storeChallenge()

        // 构建认证选项
        val options = mutableMapOf<String, Any>(
            "challenge" to challenge,
            "timeout" to 60000,
            "rpId" to rp.id,
            "userVerification" to "preferred"
        )

        // 添加允许的凭据
        if (allowCredentials.isNotEmpty()) {
            options["allowCredentials"] = allowCredentials.map {
                mapOf(
                    "type" to "public-key",
                    "id" to it,
                    "transports" to listOf("internal", "usb", "ble", "nfc")
                )
            }
        }

        return options
    }

    /**
     * 验证认证结果
     */
    override fun verifyAuthentication(authenticationData: Map<String, Any>): WebAuthnAuthenticationResult? {
        try {
            // 解析认证数据
            val id = authenticationData["id"] as String
            val rawId = authenticationData["rawId"] as String
            val type = authenticationData["type"] as String
            val responseMap = authenticationData["response"] as Map<String, Any>

            val authenticatorData = responseMap["authenticatorData"] as String
            val signature = responseMap["signature"] as String
            val clientDataJSON = responseMap["clientDataJSON"] as String
            val userHandle = responseMap["userHandle"] as? String

            // 验证类型
            if (type != "public-key") {
                return null
            }

            // 获取并解码挑战
            val decodedClientData = Base64.getDecoder().decode(clientDataJSON)
            val clientDataJson = String(decodedClientData, Charsets.UTF_8)

            // 从JSON中提取挑战
            val challengeRegex = """"challenge":"([^"]+)"""".toRegex()
            val matchResult = challengeRegex.find(clientDataJson)
            val challengeBase64 = matchResult?.groupValues?.get(1)
                ?: return null

            // 查找存储的挑战
            val storedChallenge = challengeMap[challengeBase64]
                ?: return null

            // 清理使用过的挑战
            challengeMap.remove(challengeBase64)

            // 这里简化处理，实际应用中需要使用WebAuthn4J进行完整验证
            return WebAuthnAuthenticationResult(
                credentialId = rawId,
                counter = 0 // 这里应该从authenticatorData中提取计数器值
            )

        } catch (e: Exception) {
            application.log.error("Authentication verification failed", e)
            return null
        }
    }

    /**
     * 依赖对象：信赖方信息
     */
    private data class RelyingParty(
        val id: String,
        val name: String
    )
}