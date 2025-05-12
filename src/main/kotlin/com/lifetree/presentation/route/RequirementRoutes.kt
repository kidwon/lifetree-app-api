// 修改后的需求路由 (RequirementRoutes.kt) - 添加协议相关路由
package com.lifetree.presentation.route

import com.lifetree.presentation.controller.RequirementController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

// 需求路由
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
                val result = requirementController.createRequirement(createDto, principal)
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

            // 新增：单独更新需求协议
            put("/{id}/agreement") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")

                // 获取协议内容
                val agreementData = call.receive<Map<String, String?>>()
                val agreement = agreementData["agreement"]

                val result = requirementController.updateRequirementAgreement(id, agreement)
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

// 需求申请相关路由
fun Route.requirementApplicationRoutes() {
    val requirementController: RequirementController by inject()

    // 需求申请相关路由
    route("/api/requirements") {
        // 需要身份验证的路由
        authenticate {
            // 获取当前用户创建的所有需求（包含申请信息）
            get("/my-requirements") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val requirements = requirementController.getMyRequirementsWithApplications(principal)
                call.respond(requirements)
            }

            // 获取当前用户申请的所有需求
            get("/my-applications") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val applications = requirementController.getMyApplications(principal)
                call.respond(applications)
            }

            // 获取指定需求的所有申请
            get("/{id}/applications") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                try {
                    val applications = requirementController.getApplicationsByRequirement(id, principal)
                    call.respond(applications)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "An error occurred"))
                }
            }

            // 申请接受需求
            post("/{id}/accept") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing id")

                try {
                    val requirement = requirementController.acceptRequirement(id, principal)
                    if (requirement != null) {
                        call.respond(requirement)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Requirement not found")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "An error occurred"))
                }
            }

            // 同意申请
            post("/{id}/applications/{applicationId}/approve") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing id")
                val applicationId = call.parameters["applicationId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing applicationId")

                try {
                    val requirement = requirementController.approveApplication(id, applicationId, principal)
                    if (requirement != null) {
                        call.respond(requirement)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Requirement not found")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "An error occurred"))
                }
            }

            // 拒绝申请
            post("/{id}/applications/{applicationId}/reject") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing id")
                val applicationId = call.parameters["applicationId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing applicationId")

                try {
                    val requirement = requirementController.rejectApplication(id, applicationId, principal)
                    if (requirement != null) {
                        call.respond(requirement)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Requirement not found")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "An error occurred"))
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
        requirementApplicationRoutes()
    }
}