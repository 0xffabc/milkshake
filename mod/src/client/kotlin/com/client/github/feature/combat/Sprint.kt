package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

import com.client.github.feature.Module
import com.client.github.feature.Criticals

object Sprint {
  private lateinit var mc: MinecraftClient
  private var tapCooldown = 0

  val mod = Module("Combat", "Sprint")
  val tap = Module("Combat", "Auto blink")

  init {
    mc = MinecraftClient.getInstance()
  }

  private fun tap() {
    mc?.player?.setSprinting(false)
    mc.options.backKey.setPressed(true)
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    if (tapCooldown-- > 0) {
      return tap()
    } else if (mc.options.backKey.isPressed() && tapCooldown <= 1 && tapCooldown >= -1) {
      mc.options.backKey.setPressed(false)
    }

    val swingProgress = mc?.player?.getAttackCooldownProgress(0.5f) ?: return

    if (tap.enabled() && swingProgress <= 0.3f) {
      tap()
 
      tapCooldown = 3

      return
    }

    mc?.player?.setSprinting(true)
  }
}
