package com.nicolas.rd_anunciar

import com.nicolas.rd_anunciar.command.AnnounceCommand
import com.nicolas.rd_anunciar.database.AnnouncementDatabase
import com.nicolas.rd_anunciar.listener.AdminJoinListener
import com.nicolas.rd_anunciar.updater.UpdateChecker
import com.nicolas.rd_anunciar.utils.Constants
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException

class Main : JavaPlugin() {

    private var announcementDatabase: AnnouncementDatabase? = null
    var hasVersionAvailable: HashMap<String, Boolean> = hashMapOf(
        Constants.HAS_VERSION to false
    )
    var versions: HashMap<String, String> = hashMapOf()

    override fun onEnable() {

        UpdateChecker(this, Constants.RESOURCE_ID).getVersion { version ->
            if (this.description.version.equals(version)) {
                logger.info("Nenhuma atualização disponível.")
            } else {
                hasVersionAvailable[Constants.HAS_VERSION] = true
                versions[Constants.NEW_VERSION] = version
                logger.info("Existe uma atualização disponível!")
            }
        }

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        connectionDatabase()
        server.getPluginCommand("anunciar").executor = AnnounceCommand(this)

        registerEvents()

        saveDefaultConfig()
    }

    private fun registerEvents() {
        Bukkit.getPluginManager().registerEvents(AdminJoinListener(this), this)
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
        hasVersionAvailable.clear()
        versions.clear()
    }

}
