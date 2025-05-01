// DatabaseConfig.kt - 数据库配置
package com.lifetree.infrastructure.config

import com.lifetree.infrastructure.persistence.table.Requirements
import com.lifetree.infrastructure.persistence.table.Results
import com.lifetree.infrastructure.persistence.table.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
//    println("kkkkk")
//    println(environment.config.propertyOrNull("database.driver"))
    val config = environment.config.config("database")
    println(environment.config.property("database.driver").getString())
    val driverClassName = environment.config.property("database.driver").getString()
    val jdbcURL = environment.config.property("database.url").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

//    val driverClassName = "org.postgresql.Driver"
//    val jdbcURL = "jdbc:postgresql://localhost:5432/lifetree"
//    val user = "postgres"
//    val password = "password"

    val hikariConfig = HikariConfig().apply {
        this.driverClassName = driverClassName
        this.jdbcUrl = jdbcURL
        this.username = user
        this.password = password
        this.maximumPoolSize = 10
        this.isAutoCommit = false
        this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        this.validate()
    }

    val dataSource = HikariDataSource(hikariConfig)
    val database = Database.connect(dataSource)

    // 初始化数据库表结构
    transaction(database) {
        SchemaUtils.create(Users)
        SchemaUtils.create(Requirements)
        SchemaUtils.create(Results)
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

