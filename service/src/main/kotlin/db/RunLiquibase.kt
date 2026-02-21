package org.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

/**
 * Точка входа для запуска только миграций Liquibase (без старта API).
 * Использует переменные окружения: DB_JDBC_URL, DB_USER, DB_PASSWORD.
 * Запуск: ./gradlew runMigrations (или с указанием env вручную).
 */
fun main() {
    val jdbcUrl = System.getenv("DB_JDBC_URL")
        ?: error("DB_JDBC_URL не задан. Пример: export DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper")
    val user = System.getenv("DB_USER") ?: error("DB_USER не задан")
    val password = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD не задан")

    val config = HikariConfig().apply {
        this.jdbcUrl = jdbcUrl
        driverClassName = "org.postgresql.Driver"
        username = user
        this.password = password
        maximumPoolSize = 1
    }

    val dataSource = HikariDataSource(config)
    try {
        runLiquibaseMigrations(dataSource)
        println("Liquibase: миграции выполнены успешно.")
    } finally {
        dataSource.close()
    }
}
