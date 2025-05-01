package com.lifetree.application.mapper

import com.lifetree.application.dto.user.UserDto
import com.lifetree.domain.model.user.User
import java.time.format.DateTimeFormatter

object UserMapper {
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    fun toDto(user: User): UserDto {
        return UserDto(
            id = user.id.toString(),
            email = user.getEmail(),
            name = user.getName(),
            role = user.getRole().name,
            createdAt = user.createdAt.format(dateTimeFormatter),
            updatedAt = user.getUpdatedAt().format(dateTimeFormatter)
        )
    }
}