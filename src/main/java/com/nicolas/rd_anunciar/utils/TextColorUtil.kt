package com.nicolas.rd_anunciar.utils

import org.bukkit.ChatColor

object TextColorUtil {

    fun text(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}