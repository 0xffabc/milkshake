package com.client.github.feature

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.util.ActionResult

import com.client.github.feature.Module

object Criticals {
    /**
      * Crit requirements:
      * 1. Weapon reload > 0.9
      * 2. Fall distance > 0f
      * 3. Not on ground
      * 4. Not climbing
      * 5. Not touching water
      * 6. Doesn't have blindness
      * 7. Not in a vehicle
      * 8. Is a LivingEntity
      * 9. Not sprinting
    **/

  private lateinit var mc: MinecraftClient
  val mod = Module("Combat", "Criticals")

  fun bootstrap() {
    mc = MinecraftClient.getInstance()

    AttackEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
      if (mc.player == player && !player.isSpectator() && world.isClient()) {
        prepare()
      }

      ActionResult.PASS
    }
  }

  fun prepare() {
    if (!mod.enabled()) return
    if (mc.player == null) return

    val player = mc.player as Entity
    
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY() + 0.1, player.getZ(), true
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY(), player.getZ(), false
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY() + 0.01, player.getZ(), false
    ))
    mc?.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
      player.getX(), player.getY(), player.getZ(), false
    ))
  }
}
