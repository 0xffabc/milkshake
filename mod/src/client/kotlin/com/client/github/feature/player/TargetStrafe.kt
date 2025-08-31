package com.client.github.feature.player

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d

import com.client.github.feature.Module

import kotlin.math.*

object TargetStrafe : Module(
  "Player",
  "Strafe"
) {
  internal val mc = MinecraftClient.getInstance()

  var strafeSpeed = 0.26

  fun tick() {
    if (disabled()) return

    val player = mc.player ?: return

    if (player.isOnGround()) return

    val vel = player.getVelocity()

    var movementVec = Vec3d.ZERO

    val camera = mc?.gameRenderer?.getCamera() ?: return

    val pitch = camera.getPitch()
    val yaw = camera.getYaw()

    val straight = Vec3d.fromPolar(pitch, yaw)
    val gay = Vec3d.fromPolar(0f, yaw + 90f)

    if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
    if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
    if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
    if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)
    
    player.setVelocity(
      movementVec.getX() * strafeSpeed,
      vel.getY(),
      movementVec.getZ() * strafeSpeed
    )
  }
}
