package com.lifetree.presentation.route

import com.lifetree.presentation.controller.ResultController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.resultRoutes() {
    val resultController: ResultController by inject()

    route("/api/results") {
        // 需要身份验证的路由
        authenticate {
            // 获取所有结果
            get {
                val results = resultController.getAllResults()
                call.respond(results)
            }

            // 获取单个结果
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val result = resultController.getResultById(id)
                if (result != null) {
                    call.respond(result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Result not found")
                }
            }

            // 获取用户的所有结果
            get("/user/{userId}") {
                val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")

                val results = resultController.getResultsByUser(userId)
                call.respond(results)
            }

            // 获取需求的所有结果
            get("/requirement/{requirementId}") {
                val requirementId = call.parameters["requirementId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing requirementId")

                val results = resultController.getResultsByRequirement(requirementId)
                call.respond(results)
            }

            // 创建新结果
            post {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val createDto = call.receive<com.lifetree.application.dto.result.CreateResultDto>()
                val result = resultController.createResult(createDto, principal)

                if (result != null) {
                    call.respond(HttpStatusCode.Created, result)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user")
                }
            }

            // 更新结果
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing id")
                val updateDto = call.receive<com.lifetree.application.dto.result.UpdateResultDto>()

                val result = resultController.updateResult(id, updateDto)
                if (result != null) {
                    call.respond(result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Result not found")
                }
            }

            // 删除结果
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing id")

                val success = resultController.deleteResult(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Result not found")
                }
            }
        }
    }
}