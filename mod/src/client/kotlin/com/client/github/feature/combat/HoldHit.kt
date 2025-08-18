package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

import com.client.github.feature.Module
import com.client.github.feature.Criticals

object HoldHit {
  private lateinit var mc: MinecraftClient
  private var hitCooldown = 0

  val mod = Module("Combat", "Hold hit")

  fun bootstrap() {
    mc = MinecraftClient.getInstance()

    Criticals.bootstrap()
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    val swingProgress = mc!!.player!!.getAttackCooldownProgress(0.5f)

    mc.options.attackKey.setPressed(false)

    if (hitCooldown-- > 0) return
    if (swingProgress < 0.9f) return

    Criticals.prepare()
    mc.options.attackKey.setPressed(true)
    hitCooldown = 9 
  }
}
