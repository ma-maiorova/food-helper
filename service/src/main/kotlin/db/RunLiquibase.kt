package org.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File

/**
 * Точка входа для запуска только миграций Liquibase (без старта API).
 * Берёт настройки из переменных окружения или из файла .env в текущей директории (при запуске из service/).
 * Запуск: cd service && ./gradlew runMigrations
 */
fun main() {
    val env = loadEnvWithFallback()
    val jdbcUrl = env["DB_JDBC_URL"]
        ?: error("DB_JDBC_URL не задан. Задайте переменные окружения или создайте service/.env (см. .env.example). Пример: DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper")
    val user = env["DB_USER"] ?: error("DB_USER не задан")
    val password = env["DB_PASSWORD"] ?: error("DB_PASSWORD не задан")

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
    } catch (e: Exception) {
        val isPostgresHost = jdbcUrl.contains("//postgres:")
        val hasConnectionError = e.message?.contains("postgres") == true ||
            e.cause?.message?.contains("postgres") == true ||
            e.cause?.cause?.message?.contains("postgres") == true
        if (isPostgresHost && hasConnectionError) {
            throw IllegalStateException(
                "Не удалось подключиться к БД. Хост 'postgres' доступен только внутри Docker. " +
                    "При запуске миграций с хоста (./gradlew runMigrations) укажите 127.0.0.1 и порт маппинга, например: " +
                    "export DB_JDBC_URL=jdbc:postgresql://127.0.0.1:5435/food_helper",
                e
            )
        }
        throw e
    } finally {
        dataSource.close()
    }
}

private fun loadEnvWithFallback(): Map<String, String> {
    val keys = listOf("DB_JDBC_URL", "DB_USER", "DB_PASSWORD")
    val fromFile = loadEnvFile()
    return keys.associateWith { key ->
        System.getenv(key)?.takeIf { it.isNotBlank() } ?: fromFile[key] ?: ""
    }
}

private fun loadEnvFile(): Map<String, String> {
    val dir = System.getProperty("user.dir")
    val envFile = File(dir, ".env").takeIf { it.isFile } ?: return emptyMap()
    return envFile.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .mapNotNull { line ->
            val eq = line.indexOf('=')
            if (eq <= 0) null else line.substring(0, eq).trim() to line.substring(eq + 1).trim().removeSurrounding("\"", "\"")
        }
        .toMap()
}
