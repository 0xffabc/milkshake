package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.*
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.ActionResult
import net.minecraft.client.render.Camera
import net.minecraft.client.network.OtherClientPlayerEntity

import com.mojang.authlib.GameProfile

import com.client.github.feature.Module

import kotlin.uuid.Uuid
import java.util.UUID

import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

/**
 * For some retarded reason it doesn't work on PlayerEntity and Entity (yaw glitches)
 * Also for some retarded reason my build ignores the existence of toJavaUuid
**/

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
class CameraEntity(
  val _world: ClientWorld,
  val _gameProfile: GameProfile
) : OtherClientPlayerEntity(_world, _gameProfile) { 
  private val mc: MinecraftClient = MinecraftClient.getInstance()

  init {
    val uuid: Uuid = Uuid.random()

    //setUuid(uuid.toJavaUuid())
  }
}

object FreeCam {
  val mod = Module(
    "Utility",
    "Free cam"
  )

  private lateinit var mc: MinecraftClient
  private lateinit var camEntity: CameraEntity
  private lateinit var camPos: Vec3d

  private var wasActive: Boolean = false

  init {
    mc = MinecraftClient.getInstance()

    AttackEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
      if (mod.enabled()) {
        return@register ActionResult.FAIL
      }

      ActionResult.PASS
    }

    WorldRenderEvents.AFTER_SETUP.register { ctx ->
      synchronizeRotation()
      synchronizeMovement() 
    }
  }

  internal fun synchronizeMovement() {
    if (!::camEntity.isInitialized) return

    val player = mc?.player ?: return

    val pitch = player.getPitch()

    val yaw = player.getYaw()

    var movementVec = Vec3d.ZERO

    val straight = Vec3d.fromPolar(pitch, yaw)
    val gay = Vec3d.fromPolar(0f, yaw + 90f)

    if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
    if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
    if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
    if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)
    
    movementVec = movementVec.multiply(1.0, 0.0, 1.0)

    if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
    else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

    // 20 / (1 / 3) = 60
    camEntity.setPosition(movementVec.multiply(1.0 / 3.0).add(
      camEntity.getPos()
    ))
  }

  internal fun synchronizeRotation() {
    if (!::camEntity.isInitialized) return

    val player = mc?.player ?: return

    val pitch = player.getPitch()

    val yaw = player.getYaw()

    camEntity.headYaw = yaw
    camEntity.bodyYaw = yaw
    camEntity.setAngles(yaw, pitch)
  }

  private fun revokeCamera() {
    mc?.setCameraEntity(mc!!.player)

    camEntity.discard()

    mc?.worldRenderer?.reload()
  }

  private fun setCamera() {
    val player = mc.player ?: return
    val world = mc.world ?: return

    if (mc.getCameraEntity() is CameraEntity) return

    camEntity = CameraEntity(world, player.getGameProfile())
    
    mc?.setCameraEntity(camEntity)

    with(player.getPos()) {
      camEntity.setPos(getX(), getY(), getZ())

      camPos = this
    }

    world.addEntity(camEntity)
  }

  internal fun updateCamera() {
    if (!::camEntity.isInitialized) return
    if (!::camPos.isInitialized) return

    val camera = mc?.player ?: return

    camera.setVelocity(Vec3d.ZERO)
  }

  fun tick() { 
    if (!mod.enabled()) {
      if (wasActive) {
        wasActive = false

        revokeCamera()
      }

      return
    }

    if (!wasActive) {
      wasActive = true

      setCamera()
    }

    updateCamera()
  }
}
