package org.example.config

data class DatabaseConfig(
    val jdbcUrl: String,
    val driverClassName: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int = 10
)
