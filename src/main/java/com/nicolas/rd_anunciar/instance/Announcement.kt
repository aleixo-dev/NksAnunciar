package com.nicolas.rd_anunciar.instance

import com.nicolas.rd_anunciar.Main
import com.nicolas.rd_anunciar.utils.TextColorUtil
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class Announcement(
    private val messages: List<String>,
    private val announcer : String,
    private val plugin : Main
) : BukkitRunnable() {

    private val stringBuilder = StringBuilder()

    override fun run() {

        for (message in messages) {
            stringBuilder.append(message)
            stringBuilder.append(" ")
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage("\n")
            player.sendMessage(
                TextColorUtil.text("${plugin.config.getString("mensagens.anuncio")} &f$announcer: &6$stringBuilder")
            )
            player.sendMessage("\n")
        }

        cancel()
    }
}