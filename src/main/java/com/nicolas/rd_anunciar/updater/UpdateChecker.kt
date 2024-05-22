package com.nicolas.rd_anunciar.updater

import com.nicolas.rd_anunciar.Main
import org.bukkit.Bukkit
import java.io.IOException
import java.net.URL
import java.util.Scanner
import java.util.function.Consumer

class UpdateChecker(
    private val plugin: Main,
    private val resourceId: Int
) {

    fun getVersion(consumer: Consumer<String>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            runCatching {
                val inputStream = URL("$BASE_URL$resourceId/~").openStream()
                val scanner = Scanner(inputStream)
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next())
                }
            }.onFailure { exception ->
                plugin.logger.info("Não foi possível buscar por atualizações: ${exception.message}")
            }
        }
    }

    companion object {
        const val BASE_URL = "https://api.spigotmc.org/legacy/update.php?resource="
    }
}