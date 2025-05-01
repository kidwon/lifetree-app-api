package com.lifetree.infrastructure.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Results : Table() {
    val id = uuid("id").uniqueIndex()
    val title = varchar("title", 255)
    val description = text("description")
    val status = varchar("status", 50)
    val relatedRequirementId = uuid("related_requirement_id")
        .references(Requirements.id)
        .nullable()
    val createdBy = uuid("created_by").references(Users.id)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}