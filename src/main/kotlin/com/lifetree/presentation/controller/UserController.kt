package com.lifetree.presentation.controller

import com.lifetree.application.dto.user.CreateUserDto
import com.lifetree.application.dto.user.LoginResponseDto
import com.lifetree.application.dto.user.UpdateUserDto
import com.lifetree.application.dto.user.UserCredentialsDto
import com.lifetree.application.dto.user.UserDto
import com.lifetree.application.service.UserApplicationService
import com.lifetree.domain.model.user.UserId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class UserController(
    private val userService: UserApplicationService
) {
    suspend fun register(createUserDto: CreateUserDto): UserDto {
        return userService.register(createUserDto)
    }

    suspend fun login(credentials: UserCredentialsDto): LoginResponseDto {
        return userService.login(credentials)
    }

    suspend fun getCurrentUser(principal: JWTPrincipal): UserDto? {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return userService.getUserById(userIdObj)
    }

    suspend fun getUserById(id: String): UserDto? {
        val userId = try {
            UserId.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return userService.getUserById(userId)
    }

    suspend fun getUserByEmail(email: String): UserDto? {
        return userService.getUserByEmail(email)
    }

    suspend fun getAllUsers(): List<UserDto> {
        return userService.getAllUsers()
    }

    suspend fun updateCurrentUser(principal: JWTPrincipal, updateDto: UpdateUserDto): UserDto? {
        val userId = principal.payload.getClaim("id").asString()
        val userIdObj = try {
            UserId.fromString(userId)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return userService.updateUser(userIdObj, updateDto)
    }

    suspend fun isAdmin(principal: JWTPrincipal): Boolean {
        val role = principal.payload.getClaim("role").asString()
        return role == "ADMIN"
    }
}