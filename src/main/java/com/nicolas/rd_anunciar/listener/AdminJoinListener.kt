package com.nicolas.rd_anunciar.listener

import com.nicolas.rd_anunciar.Main
import com.nicolas.rd_anunciar.utils.Constants
import com.nicolas.rd_anunciar.utils.TextColorUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AdminJoinListener(private val plugin: Main) : Listener {

    @EventHandler
    fun onAdminJoin(playerJoinEvent: PlayerJoinEvent) {

        if (playerJoinEvent.player.isOp && plugin.hasVersionAvailable[Constants.HAS_VERSION]!!) {
            playerJoinEvent.player.sendMessage(
                TextColorUtil.text(
                    "\n&a[NksAnunciar] &7Uma nova atualização está disponível: " +
                            "&a${plugin.versions[Constants.NEW_VERSION]} &7e você está na &a${plugin.description.version}\n&f"
                )
            )
        }
    }
}