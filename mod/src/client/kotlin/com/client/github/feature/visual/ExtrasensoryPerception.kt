package com.client.github.feature.visual

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.world.ClientWorld
import net.minecraft.client.render.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper

import com.mojang.blaze3d.systems.RenderSystem

import com.client.github.feature.Module
import com.client.github.util.Geometry
import com.client.github.util.Point

import kotlin.math.*
import org.joml.*

fun toDirVec(pitch: Double, yaw: Double): Vec3d {
  val yawCos = MathHelper.cos(yaw.toFloat()).toDouble()
  val yawSin = MathHelper.sin(yaw.toFloat()).toDouble()

  val pitchSin = MathHelper.sin(pitch.toFloat()).toDouble()
  val npitchCos = MathHelper.cos(PI.toFloat() + pitch.toFloat()).toDouble()

  return Vec3d(
    yawSin * npitchCos,
    pitchSin,
    yawCos * npitchCos
  )
}

fun drawLine(
  matrices: MatrixStack,
  vertexConsumers: VertexConsumerProvider,
  cameraX: Double,
  cameraY: Double,
  cameraZ: Double,
  entityPos: Vec3d,
  targetPos: Vec3d,
  color: Int
) {
  val layer = RenderLayer.getDebugLineStrip(5.0)
  val consumer = vertexConsumers.getBuffer(layer)

  consumer.vertex(matrices.peek(), (entityPos.x - cameraX).toFloat(), (entityPos.y - cameraY).toFloat(), (entityPos.z - cameraZ).toFloat()).color(color)
  consumer.vertex(matrices.peek(), (targetPos.x - cameraX).toFloat(), (targetPos.y - cameraY).toFloat(), (targetPos.z - cameraZ).toFloat()).color(color) 
}

fun toRadians(deg: Double): Double = deg / 180.0 * PI

object ExtrasensoryPerception {
  private val modPlayer = Module(
    "Visual",
    "Player tracers"
  )

  private val modMob = Module(
    "Visual",
    "Mob tracers"
  )

  private val modItem = Module(
    "Visual",
    "Item tracers"
  )

  private lateinit var mc: MinecraftClient
  private var wasBobblingOn: Boolean = false

  fun bootstrap() {
    mc = MinecraftClient.getInstance()
  }

  fun render(stack: MatrixStack, consumers: VertexConsumerProvider) {
    if (!modPlayer.enabled() && !modMob.enabled() && !modItem.enabled()) {
      if (wasBobblingOn) mc.options.getBobView().setValue(true)

      return
    }

    RenderSystem.disableDepthTest()
    RenderSystem.lineWidth(3.5f)

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()
 
      val camera = mc.getBlockEntityRenderDispatcher().camera
      val cameraPos = camera.getPos()

      val tickDelta = camera.getLastTickDelta()

      val camYaw = -MathHelper.wrapDegrees(camera.getYaw().toDouble()) * MathHelper.RADIANS_PER_DEGREE - PI
      val camPitch = -MathHelper.wrapDegrees(camera.getPitch().toDouble()) * MathHelper.RADIANS_PER_DEGREE

      val dirVec = toDirVec(camPitch, camYaw)

      if (mc.options.getBobView().getValue()) {
        wasBobblingOn = true

        mc.options.getBobView().setValue(false)
      }

      for (entity in entities) {
        val color = when {
          entity.isPlayer() && modPlayer.enabled() -> 0xFFB42828
          entity is LivingEntity && modMob.enabled() -> 0xFFB4B428
          modItem.enabled() && !(entity is LivingEntity) -> 0xFF96F0FF
          else -> continue
        }.toInt()

        val pos = entity.getLerpedPos(tickDelta) ?: continue
        
        drawLine(
          stack, consumers,
          cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(),
          pos.add(0.0, entity.height / 2.0, 0.0),
          cameraPos.add(dirVec),
          color
        )
      }

      RenderSystem.setShader(GameRenderer::getPositionColorProgram)
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F) 
    }

    RenderSystem.enableDepthTest()
  }
}
