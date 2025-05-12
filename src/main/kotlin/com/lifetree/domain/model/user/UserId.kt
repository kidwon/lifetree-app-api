package com.lifetree.domain.model.user

import java.util.*

data class UserId(val value: UUID) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromString(id: String): UserId {
            return try {
                UserId(UUID.fromString(id))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid UserId format")
            }
        }
    }

    override fun toString(): String = value.toString()
}