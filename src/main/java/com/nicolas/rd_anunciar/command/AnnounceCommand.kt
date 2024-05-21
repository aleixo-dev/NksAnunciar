package com.nicolas.rd_anunciar.command

import com.nicolas.rd_anunciar.Main
import com.nicolas.rd_anunciar.instance.Announcement
import com.nicolas.rd_anunciar.utils.TextColorUtil
import com.nicolas.rd_anunciar.utils.linkChecker
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

class AnnounceCommand(private val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender?, p1: Command?, p2: String?, args: Array<out String>): Boolean {

        val player = sender as? Player ?: return true

        if (args.isNotEmpty() && args[0] == "reload" && player.hasPermission("nksanunciar.reload")) {
            plugin.reloadConfig()
            player.sendMessage(TextColorUtil.text("&6&lnksAnunciar &7- &aPlugin recarregado com sucesso!"))
            return true
        }

        if (args.isNotEmpty()) {
            args.forEach {
                if (it.linkChecker()) {
                    player.sendMessage(TextColorUtil.text("&cVocê não pode enviar link no anúncio."))
                    return true
                }
            }
        }

        val currentTime = System.currentTimeMillis()
        val lasTimeUpdate = plugin.database()?.getPlayerAnnouncementTime(player) ?: 0L

        if (commandIsAvailable(currentTime, lasTimeUpdate)) {

            if (!player.hasPermission("nksanunciar.usar")) {
                player.sendMessage(TextColorUtil.text(plugin.config.getString("mensagens.permissao")))
                return true
            }

            if (args.isEmpty()) {
                player.sendMessage(TextColorUtil.text(plugin.config.getString("mensagens.ajuda")))
                return true
            }

            Announcement(args.toList(), player.displayName, plugin).runTask(plugin)
            plugin.database()?.updatePlayerAnnouncementTime(player, currentTime)

        } else {
            showElapsedTime(currentTime, lasTimeUpdate, player)
        }

        return true
    }

    private fun commandIsAvailable(currentTime: Long, lasTimeUpdate: Long) =
        currentTime - lasTimeUpdate >= TimeUnit.MINUTES.toMillis(plugin.config.getLong("config.cooldown-timer"))

    private fun showElapsedTime(currentTime: Long, lasTimeUpdate: Long, player: Player) {

        val elapsedTime = currentTime - lasTimeUpdate
        val remainingTime = TimeUnit.MINUTES.toMillis(5) - elapsedTime

        if (remainingTime > 0) {

            val remainMinutes = remainingTime / (60 * 1000)
            val remainSeconds = (remainingTime % (60 * 1000)) / 1000

            val remainTime = plugin.config.getString("mensagens.tempo-decorrido")
            val firstReplace = remainTime.replace("{remainMinutes}", remainMinutes.toString())
            val resultTextFormatted = firstReplace.replace("{remainSeconds}", remainSeconds.toString())

            player.sendMessage(
                TextColorUtil.text("${plugin.config.getString("mensagens.anuncio")} $resultTextFormatted")
            )
        }
    }
}