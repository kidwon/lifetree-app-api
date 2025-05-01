// PasswordEncoder.kt - 密码编码器
package com.lifetree.infrastructure.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class PasswordEncoder {
    private val random = SecureRandom()

    /**
     * 为密码生成哈希值
     */
    fun encode(password: String): String {
        val salt = ByteArray(16)
        random.nextBytes(salt)

        val md = MessageDigest.getInstance("SHA-512")
        md.update(salt)

        val hashedPassword = md.digest(password.toByteArray())

        return Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hashedPassword)
    }

    /**
     * 验证密码与存储的哈希值是否匹配
     */
    fun matches(password: String, encodedPassword: String): Boolean {
        val parts = encodedPassword.split("$")
        if (parts.size != 2) return false

        val salt = Base64.getDecoder().decode(parts[0])
        val hashedPassword = Base64.getDecoder().decode(parts[1])

        val md = MessageDigest.getInstance("SHA-512")
        md.update(salt)

        val newHashedPassword = md.digest(password.toByteArray())

        return newHashedPassword.contentEquals(hashedPassword)
    }
}