package com.client.github.feature.utility

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.*
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import net.minecraft.util.math.BlockPos
import net.minecraft.util.ActionResult
import net.minecraft.client.render.Camera

import com.mojang.authlib.GameProfile

import com.client.github.feature.Module

import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

class CameraEntity(
  val _world: World,
  val _blockPos: BlockPos,
  val _yaw: Float,
  val _gameProfile: GameProfile
) : PlayerEntity(_world, _blockPos, _yaw, _gameProfile) {
  override fun isSpectator(): Boolean = false
  override fun isCreative(): Boolean = false
  override fun isAttackable(): Boolean = false
  override fun handleAttack(attacker: Entity): Boolean = true
  override fun tick() {}
  override fun tickMovement() {}
  override fun changeLookDirection(x: Double, y: Double) {}

  private var pulsePitch = 0f
  private var pulseYaw = _yaw

  override fun getPitch(): Float = pulsePitch
  override fun getYaw(): Float = pulseYaw

  private lateinit var mc: MinecraftClient

  init {
    mc = MinecraftClient.getInstance()
  }

  internal fun __setEulerAngles(pitch: Float, yaw: Float) {
    pulsePitch = pitch
    pulseYaw = yaw

    setRotation(yaw, pitch)
    super.setRotation(yaw, pitch)
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
    }
  }

  internal fun synchronizeRotation() {
    if (!::camEntity.isInitialized) return

    val player = mc?.player ?: return

    val pitch = player.getPitch()
    val yaw = player.getYaw()

    camEntity.__setEulerAngles(pitch, yaw)
  }

  private fun revokeCamera() {
    mc?.setCameraEntity(mc!!.player)
  }

  private fun setCamera() {
    val player = mc.player ?: return
    val world = mc.world ?: return

    if (mc.getCameraEntity() is CameraEntity) return

    camEntity = CameraEntity(world, player.getBlockPos(), player.getYaw(), mc.getGameProfile() ?: return)
    
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

    var movementVec = Vec3d.ZERO

    val camera = mc?.player ?: return

    camera.setVelocity(Vec3d.ZERO)

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

    camPos = camPos.add(movementVec.normalize())

    camEntity.setPosition(camPos)

    synchronizeRotation()
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
