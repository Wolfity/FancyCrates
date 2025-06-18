package me.wolfity.fancycrates.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.wolfity.developmentutil.sql.PlayerRegistry
import me.wolfity.fancycrates.db.type.DatabaseType
import me.wolfity.fancycrates.plugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager {

    fun init() {
        val databaseType: DatabaseType = try {
            DatabaseType.valueOf(plugin.dbConfig.getString("database-type").uppercase())
        } catch (e: Exception) {
            throw IllegalArgumentException("Database type '${plugin.dbConfig.getString("database-type")}' is invalid! Choose between ${DatabaseType.entries.map { it.name }}")
        }

        val config = HikariConfig().apply {
            when (databaseType) {
                DatabaseType.SQLITE -> {
                    jdbcUrl = "jdbc:sqlite:plugins/${plugin.name}/database.db"
                    driverClassName = "org.sqlite.JDBC"
                }

                DatabaseType.MYSQL -> {
                    jdbcUrl = plugin.dbConfig.getString("mysql.jdbc-url")
                    username = plugin.dbConfig.getString("mysql.username")
                    password = plugin.dbConfig.getString("mysql.password")
                    driverClassName = "com.mysql.cj.jdbc.Driver"
                }
            }

            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(PlayerRegistry, CrateLocations)
        }
    }
}
