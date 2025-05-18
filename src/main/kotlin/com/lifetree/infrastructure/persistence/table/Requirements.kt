// Requirements.kt - 数据库表定义 (添加协议按钮文本字段)
package com.lifetree.infrastructure.persistence.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Requirements : Table() {
    val id = uuid("id").uniqueIndex()
    val title = varchar("title", 255)
    val description = text("description")
    val status = varchar("status", 50)
    val agreement = text("agreement").nullable() // 协议内容字段
    val agreementButtonText = varchar("agreement_button_text", 20).nullable() // 新增协议按钮文本字段
    val createdBy = uuid("created_by").references(Users.id)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}