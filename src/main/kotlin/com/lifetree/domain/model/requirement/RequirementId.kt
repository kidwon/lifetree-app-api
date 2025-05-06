// RequirementId.kt - 需求ID值对象
package com.lifetree.domain.model.requirement

import java.util.*

data class RequirementId(val value: UUID) {
    companion object {
        fun generate(): RequirementId = RequirementId(UUID.randomUUID())

        fun fromString(id: String): RequirementId {
            return try {
                RequirementId(UUID.fromString(id))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid RequirementId format")
            }
        }
    }

    override fun toString(): String = value.toString()
}
