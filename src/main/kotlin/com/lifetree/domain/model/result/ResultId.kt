package com.lifetree.domain.model.result

import java.util.*

data class ResultId(val value: UUID) {
    companion object {
        fun generate(): ResultId = ResultId(UUID.randomUUID())

        fun fromString(id: String): ResultId {
            return try {
                ResultId(UUID.fromString(id))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid ResultId format")
            }
        }
    }

    override fun toString(): String = value.toString()
}