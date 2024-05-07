package com.nicolas.rd_anunciar

import com.nicolas.rd_anunciar.command.AnnounceCommand
import com.nicolas.rd_anunciar.database.AnnouncementDatabase
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException

class Main : JavaPlugin() {

    private var announcementDatabase: AnnouncementDatabase? = null

    override fun onEnable() {

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        connectionDatabase()
        server.getPluginCommand("anunciar").executor = AnnounceCommand(this)

        saveDefaultConfig()
    }

    private fun connectionDatabase() {
        try {
            announcementDatabase = AnnouncementDatabase(this, dataFolder.absolutePath + "/database.db")
        } catch (exception: SQLException) {
            exception.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    fun database() = announcementDatabase

    override fun onDisable() {
        try {
            announcementDatabase?.closeConnection()
        } catch (exception: SQLException) {
            exception.printStackTrace()
        }
    }
}
