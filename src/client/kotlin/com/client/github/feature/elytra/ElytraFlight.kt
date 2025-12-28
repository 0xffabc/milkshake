package com.client.github.feature.elytra

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

import com.client.github.feature.Module
import com.client.github.feature.elytra.ElytraTiming
import com.client.github.feature.elytra.modes.*

import kotlin.math.*

fun toDirVec(pitch: Float, yaw: Float): Vec3d = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

object ElytraFlight {
  val mod = Module(
    "Elytra",
    "Elytra flight"
  )

  private lateinit var mc: MinecraftClient

  private fun aboutToHitGround(): Boolean? = !(0..3).all { mc!!.world!!.getBlockState(mc!!.player!!.getBlockPos()!!.down(it))!!.isAir() }

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun getMovementVector(): Vec3d? {
      var movementVec = Vec3d.ZERO

      val camera = mc?.gameRenderer?.getCamera() ?: return null

      val pitch = camera.getPitch()
      val yaw = camera.getYaw()

      val straight = Vec3d.fromPolar(pitch, yaw)
      val gay = Vec3d.fromPolar(0f, yaw + 90f)

      if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
      if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
      if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
      if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)

      movementVec = movementVec.multiply(1.0, 0.0, 1.0)

      if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
      else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

      return movementVec
  }

  init {
      Themis
      Bounce
      Angle
      Accelerate
      Packet
      Firework
  }

  fun tick() {
    if (!mod.enabled()) return

    val movementVec = getMovementVector() ?: return

    tick(movementVec)
  }

  fun tick(movementVec: Vec3d) {
    if (!mod.enabled()) return
    if (Firework.mod.enabled()) Firework.tick(movementVec)
    if (Themis.mod.enabled()) return Themis.tick(movementVec)
    if (Bounce.mod.enabled()) return Bounce.tick(movementVec)
    if (!(mc?.player?.isFallFlying() ?: false)) return

    if (Angle.mod.enabled()) Angle.tick(movementVec)

    if (Accelerate.mod.enabled()) {
        Accelerate.tick(movementVec)
    } else if (Packet.mod.enabled()) Packet.tick(movementVec)
  }
}
