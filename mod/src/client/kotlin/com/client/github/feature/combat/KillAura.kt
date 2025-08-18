package com.client.github.feature.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.util.math.Vec3d

import com.client.github.feature.Module
import com.client.github.feature.Criticals

object KillAura {
  private lateinit var mc: MinecraftClient
  private var hitCooldown = 0

  val mod = Module("Combat", "KillAura")
  val onlyFans = Module("Combat", "KillAura:OnlyCrits")

  val killauraReach = 3.0

  fun bootstrap() {
    mc = MinecraftClient.getInstance()

    Criticals.bootstrap()
  }

  fun tick() {
    if (mc.player == null) return
    if (!mod.enabled()) return

    val swingProgress = mc!!.player!!.getAttackCooldownProgress(0.5f)

    if (hitCooldown-- > 0) return
    if (swingProgress < 0.9f) return

    if (onlyFans.enabled() && (
      mc.player!!.fallDistance <= 0 ||
      mc.player!!.isOnGround()
    ) && !Criticals.mod.enabled()) return

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()
      val playerPos = (mc.player as Entity).getPos()

      for (entity in entities) {
        if (entity == null) continue
        if (entity == mc?.player) continue
        if (!entity.isAlive()) continue
        if (!(entity is LivingEntity)) continue
        if (entity is EndCrystalEntity) continue
        if (!entity.isAttackable()) continue

        val entityPos = entity.getPos()

        if (entityPos.distanceTo(playerPos) > killauraReach) continue

        Criticals.prepare()
        mc.interactionManager?.attackEntity(mc.player, entity)
        (mc.player as ClientPlayerEntity).swingHand((mc.player as ClientPlayerEntity).getActiveHand())

        hitCooldown = 9

        break
      }
    }
  }
}
