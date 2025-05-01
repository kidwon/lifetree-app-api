package com.lifetree.infrastructure.config

import com.lifetree.application.service.RequirementApplicationService
import com.lifetree.application.service.ResultApplicationService
import com.lifetree.application.service.UserApplicationService
import com.lifetree.domain.repository.RequirementRepository
import com.lifetree.domain.repository.ResultRepository
import com.lifetree.domain.repository.UserRepository
import com.lifetree.domain.service.RequirementService
import com.lifetree.domain.service.ResultService
import com.lifetree.infrastructure.persistence.repository.RequirementRepositoryImpl
import com.lifetree.infrastructure.persistence.repository.ResultRepositoryImpl
import com.lifetree.infrastructure.persistence.repository.UserRepositoryImpl
import com.lifetree.infrastructure.security.JwtProvider
import com.lifetree.infrastructure.security.PasswordEncoder
import com.lifetree.presentation.controller.RequirementController
import com.lifetree.presentation.controller.ResultController
import com.lifetree.presentation.controller.UserController
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

//fun Application.configureKoin() {
//    install(Koin) {
//        slf4jLogger()
//        modules(
//            infrastructureModule,
//            domainModule,
//            applicationModule,
//            presentationModule
//        )
//    }
//}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { this@configureKoin } // <--- 这里！注册Application到Koin
            },
            infrastructureModule,
            domainModule,
            applicationModule,
            presentationModule
        )
    }
}

// 基础设施层依赖
val infrastructureModule = module {
    // 持久化
    single<RequirementRepository> { RequirementRepositoryImpl() }
    single<ResultRepository> { ResultRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }

    // 安全
    single { JwtProvider(get<Application>().environment.config) }
    single { PasswordEncoder() }
}

// 领域层依赖
val domainModule = module {
    single { RequirementService(get()) }
    single { ResultService(get()) }
}

// 应用层依赖
val applicationModule = module {
    single { RequirementApplicationService(get()) }
    single { ResultApplicationService(get()) }
    single { UserApplicationService(get(), get(), get()) }
}

// 表现层依赖
val presentationModule = module {
    single { RequirementController(get()) }
    single { ResultController(get()) }
    single { UserController(get()) }
}