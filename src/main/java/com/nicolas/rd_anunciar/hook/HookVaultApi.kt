package com.nicolas.rd_anunciar.hook

import com.nicolas.rd_anunciar.Main
import net.milkbowl.vault.economy.Economy

class HookVaultApi(private val plugin: Main) {

    fun hook(): Economy? {
        if (plugin.server.pluginManager.getPlugin("Vault") == null) return null
        return plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }
}