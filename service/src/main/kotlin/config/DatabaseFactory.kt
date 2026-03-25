package org.example.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.example.db.runLiquibaseMigrations
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {

    lateinit var dataSource: HikariDataSource
        private set

    fun init(environment: ApplicationEnvironment) {
        val cfg = environment.config

        val jdbcUrl = System.getenv("JDBC_URL")
            ?: cfg.property("ktor.database.jdbcUrl").getString()
        val username = System.getenv("POSTGRES_USER") ?: cfg.property("ktor.database.username").getString()
        val password = System.getenv("POSTGRES_PASSWORD") ?: cfg.property("ktor.database.password").getString()

        val dbConfig = DatabaseConfig(
            jdbcUrl = jdbcUrl,
            driverClassName = cfg.property("ktor.database.driverClassName").getString(),
            username = username,
            password = password,
            maximumPoolSize = cfg.propertyOrNull("ktor.database.maximumPoolSize")?.getString()?.toInt() ?: 10
        )

        dataSource = createHikariDataSource(dbConfig)

        Database.connect(dataSource)
        runLiquibaseMigrations(dataSource)
    }

    private fun createHikariDataSource(cfg: DatabaseConfig): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = cfg.jdbcUrl
            driverClassName = cfg.driverClassName
            username = cfg.username
            password = cfg.password
            maximumPoolSize = cfg.maximumPoolSize
        }
        return HikariDataSource(hikariConfig)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
