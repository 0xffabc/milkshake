package com.client.github.utility

import net.minecraft.util.Identifier
import net.minecraft.client.toast.SystemToast.Type
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ElytraItem

import com.client.github.utility.Toast
import com.client.github.utility.CustomCapeDiscovery
import com.client.github.feature.Module

import java.net.URL
import java.io.File
import java.io.ByteArrayInputStream

import kotlin.concurrent.thread

object CustomCape {
    private val capeMap = mutableMapOf<String, Identifier?>()

    private val mod = Module("Elytra", "Custom texture from cape", true)

    fun tryParseCape(playerName: String) {
        thread(start = true) {
            val capeBytes = CustomCapeDiscovery.fetchCapeBytes(playerName)

            val identifier = Identifier.of("milkshake_cape", playerName)
            val image = NativeImage.read(ByteArrayInputStream(capeBytes))
            val texture = NativeImageBackedTexture(image)
            val textureManager = MinecraftClient.getInstance().textureManager

            textureManager.registerTexture(identifier, texture)

            capeMap[playerName] = identifier
        }
    }

    fun tryLoadCape(playerName: String): Identifier? {
        tryParseCape(playerName)

        capeMap[playerName] = null;

        Toast("Milkshake cracked capes", "Trying to load a custom cape for $playerName", Type.TUTORIAL_HINT)

        return null
    }

    fun loadCape(playerName: String): Identifier? {
        val cape = capeMap[playerName]

        if (cape != null) return cape

        return null
    }

    private fun PlayerEntity.hasElytra(): Boolean {
        return isFallFlying() || getInventory().getArmorStack(2).getItem() is ElytraItem
    }

    fun getByPlayerName(playerName: String, player: PlayerEntity): Identifier? {
        if (capeMap.containsKey(playerName)) {
            if (mod.disabled() && player.hasElytra()) {
                return null
            }

            return loadCape(playerName)
        }

        return tryLoadCape(playerName)
    }

    fun getByPlayerName(playerName: String): Identifier? {
        if (capeMap.containsKey(playerName)) return loadCape(playerName)

        return tryLoadCape(playerName)
    }
}
