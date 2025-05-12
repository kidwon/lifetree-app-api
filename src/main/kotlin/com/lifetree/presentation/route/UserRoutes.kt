package com.lifetree.presentation.route

import com.lifetree.presentation.controller.UserController
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userController: UserController by inject()

    route("/api/auth") {
        // 用户注册
        post("/register") {
            try {
                val createUserDto = call.receive<com.lifetree.application.dto.user.CreateUserDto>()
                val result = userController.register(createUserDto)
                call.respond(HttpStatusCode.Created, result)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // 用户登录
        post("/login") {
            try {
                val credentials = call.receive<com.lifetree.application.dto.user.UserCredentialsDto>()
                val response = userController.login(credentials)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to e.message))
            }
        }
    }

    route("/api/users") {
        // 需要身份验证的路由
        authenticate {
            // 获取当前用户信息
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val user = userController.getCurrentUser(principal)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            // 获取所有用户（仅管理员可访问）
            get {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val role = principal.payload.getClaim("role").asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@get
                }

                val users = userController.getAllUsers()
                call.respond(users)
            }

            // 获取单个用户
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")

                val user = userController.getUserById(id)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            // Update current user information
            put("/me") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                try {
                    val updateDto = call.receive<com.lifetree.application.dto.user.UpdateUserDto>()
                    val user = userController.updateCurrentUser(principal, updateDto)

                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request data"))
                }
            }
        }
    }
    // Admin routes
    route("/api/admin") {
        authenticate {
            // Admin-only access to all users
            get("/users") {
                // Get the principal
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                // Check if user is admin
                if (!userController.isAdmin(principal)) {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@get
                }

                // Get all users
                val users = userController.getAllUsers()
                call.respond(users)
            }
        }
    }
}