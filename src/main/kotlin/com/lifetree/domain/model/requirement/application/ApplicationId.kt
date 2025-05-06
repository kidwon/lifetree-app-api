// 申请ID值对象 (ApplicationId.kt)
package com.lifetree.domain.model.requirement.application

import java.util.*

data class ApplicationId(val value: UUID) {
    companion object {
        fun generate(): ApplicationId = ApplicationId(UUID.randomUUID())

        fun fromString(id: String): ApplicationId {
            return try {
                ApplicationId(UUID.fromString(id))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid ApplicationId format")
            }
        }
    }

    override fun toString(): String = value.toString()
}