// RequirementRoutes.kt - 需求相关路由配置
package com.lifetree.presentation.route

import com.lifetree.presentation.controller.RequirementController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.requirementRoutes() {
    val requirementController: RequirementController by inject()

    route("/api/requirements") {
        // 需要身份验证的路由
        authenticate {
            // 获取所有需求
            get {
                val requirements = requirementController.getAllRequirements()
                call.respond(requirements)
            }

            // 获取单个需求
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val requirement = requirementController.getRequirementById(id)
                if (requirement != null) {
                    call.respond(requirement)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Requirement not found")
                }
            }

            // 创建新需求
            post {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val createDto = call.receive<com.lifetree.application.dto.requirement.CreateRequirementDto>()
                val result = requirementController.createRequirement(createDto,principal)
                if (result != null) {
                    call.respond(HttpStatusCode.Created, result)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                }
            }

            // 更新需求
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val updateDto = call.receive<com.lifetree.application.dto.requirement.UpdateRequirementDto>()

                val result = requirementController.updateRequirement(id, updateDto)
                if (result != null) {
                    call.respond(result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Requirement not found")
                }
            }

            // 删除需求
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

                val success = requirementController.deleteRequirement(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Requirement not found")
                }
            }
        }
    }
}

// 路由配置函数
fun Application.configureRouting() {
    routing {
        route("/api") {
            get("/health") {
                call.respond(mapOf("status" to "UP"))
            }
        }

        // 注册各模块路由
        requirementRoutes()
        resultRoutes()
        userRoutes()
    }
}

