package com.nicolas.rd_anunciar.database

import com.nicolas.rd_anunciar.Main
import org.bukkit.entity.Player
import java.sql.*

class AnnouncementDatabase(private val plugin: Main, private val path: String) {

    private lateinit var connection: Connection

    init { initializeConnection() }

    private fun initializeConnection() {
        try {

            Class.forName("org.sqlite.JDBC")

            if (plugin.config.getString("database.type").equals("MYSQL")) {

                val urlDb = plugin.config.getString("database.host")
                val port = plugin.config.getString("database.port")
                val database = plugin.config.getString("database.db")
                val user = plugin.config.getString("database.user")
                val password = plugin.config.getString("database.password")

                val url = "jdbc:mysql://$urlDb:$port/$database?user=$user&password=$password&characterEncoding=utf8"

                connection = DriverManager.getConnection(url, user, password)

                plugin.logger.info("Conexão com o banco de dados (MYSQL) realizada com sucesso.")

            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:$path")
                plugin.logger.info("Conexão com o banco de dados (SQLite) realizada com sucesso.")
            }

            createDatabase(connection)

        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
        }
    }

    private fun createDatabase(connection: Connection) {
        try {
            val statement: Statement = connection.createStatement()
            statement.execute(
                """
                    CREATE TABLE IF NOT EXISTS announcements (
                    uuid varchar(36) PRIMARY KEY,
                    username varchar(36) NOT NULL,
                    announcementTime TIMESTAMP NOT NULL)
                """.trimIndent()
            )
            statement.close()
        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
        }
    }

    fun closeConnection() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

    private fun addAnnouncements(player: Player, currentTime: Long) {
        try {
            val preparedStatement = connection.prepareStatement(
                """
                INSERT INTO announcements (uuid, username, announcementTime) VALUES (?,?,?)
            """.trimIndent()
            )
            preparedStatement.setString(1, player.uniqueId.toString())
            preparedStatement.setString(2, player.displayName)
            preparedStatement.setTimestamp(3, Timestamp(currentTime))
            preparedStatement.executeUpdate()
        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
        }
    }

    private fun playerExits(player: Player): Boolean {
        return try {
            val preparedStatement = connection.prepareStatement(
                """
            SELECT * FROM announcements WHERE
            uuid = ?
        """.trimIndent()
            )
            preparedStatement.setString(1, player.uniqueId.toString())
            val resultSet = preparedStatement.executeQuery()
            resultSet.next()
        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
            false
        }
    }

    fun updatePlayerAnnouncementTime(player: Player, currentTime: Long) {

        if (!playerExits(player)) {
            addAnnouncements(player, currentTime)
        }

        try {
            val preparedStatement = connection.prepareStatement(
                """
                UPDATE announcements SET announcementTime = ?
                WHERE uuid = ?
            """.trimIndent()
            )
            preparedStatement.setTimestamp(1, Timestamp(currentTime))
            preparedStatement.setString(2, player.uniqueId.toString())
            preparedStatement.executeUpdate()
        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
        }
    }

    fun getPlayerAnnouncementTime(player: Player): Long {
        return try {

            val preparedStatement = connection.prepareStatement(
                """
                SELECT announcementTime FROM announcements WHERE uuid = ?
            """.trimIndent()
            )
            preparedStatement.setString(1, player.uniqueId.toString())
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getTimestamp("announcementTime").time
            } else {
                0
            }
        } catch (exception: SQLException) {
            if (plugin.config.getBoolean("debug-mode")) {
                exception.printStackTrace()
            }
            0
        }
    }
}