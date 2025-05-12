// 需求申请表定义 (RequirementApplications.kt)
package com.lifetree.infrastructure.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object RequirementApplications : Table("requirement_applications")  {
    val id = uuid("id").uniqueIndex()
    val requirementId = uuid("requirement_id").references(Requirements.id)
    val applicantId = uuid("applicant_id").references(Users.id)
    val status = varchar("status", 50)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

