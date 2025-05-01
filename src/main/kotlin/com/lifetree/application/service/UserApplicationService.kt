package com.lifetree.application.service

import com.lifetree.application.dto.user.CreateUserDto
import com.lifetree.application.dto.user.LoginResponseDto
import com.lifetree.application.dto.user.UserCredentialsDto
import com.lifetree.application.dto.user.UserDto
import com.lifetree.application.mapper.UserMapper
import com.lifetree.domain.model.user.User
import com.lifetree.domain.model.user.UserId
import com.lifetree.domain.model.user.UserRole
import com.lifetree.domain.repository.UserRepository
import com.lifetree.infrastructure.security.JwtProvider
import com.lifetree.infrastructure.security.PasswordEncoder

class UserApplicationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) {
    suspend fun register(createUserDto: CreateUserDto): UserDto {
        // 验证邮箱是否已存在
        if (userRepository.emailExists(createUserDto.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        // 创建新用户
        val user = User.create(
            id = UserId.generate(),
            email = createUserDto.email,
            passwordHash = passwordEncoder.encode(createUserDto.password),
            name = createUserDto.name,
            role = UserRole.USER
        )

        val savedUser = userRepository.save(user)
        return UserMapper.toDto(savedUser)
    }

    suspend fun login(credentials: UserCredentialsDto): LoginResponseDto {
        val user = userRepository.findByEmail(credentials.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(credentials.password, user.getPasswordHash())) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val token = jwtProvider.generateToken(user)
        return LoginResponseDto(UserMapper.toDto(user), token)
    }

    suspend fun getUserById(id: UserId): UserDto? {
        return userRepository.findById(id)
            ?.let { UserMapper.toDto(it) }
    }

    suspend fun getUserByEmail(email: String): UserDto? {
        return userRepository.findByEmail(email)
            ?.let { UserMapper.toDto(it) }
    }

    suspend fun getAllUsers(): List<UserDto> {
        return userRepository.findAll()
            .map { UserMapper.toDto(it) }
    }
}