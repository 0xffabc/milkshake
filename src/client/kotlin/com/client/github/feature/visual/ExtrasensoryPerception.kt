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

import kotlin.math.*
import org.joml.*

import org.lwjgl.opengl.GL11

val layer = RenderLayer.getDebugLineStrip(5.0)

fun toDirVec(pitch: Float, yaw: Float): Vec3d = Vec3d(0.0, 0.0, 1.0).rotateX(pitch).rotateY(yaw)

fun drawLine(
  matrices: MatrixStack,
  vertexConsumers: VertexConsumerProvider,
  entityPos: Vec3d,
  targetPos: Vec3d,
  color: Int
) {
  val consumer = vertexConsumers.getBuffer(layer)
  val matrix = matrices.peek().positionMatrix

  consumer.vertex(matrix, targetPos.x.toFloat(), targetPos.y.toFloat(), targetPos.z.toFloat()).color(color).next()
  consumer.vertex(matrix, entityPos.x.toFloat(), entityPos.y.toFloat(), entityPos.z.toFloat()).color(color).next()
  consumer.vertex(matrix, entityPos.x.toFloat(), entityPos.y.toFloat(), entityPos.z.toFloat()).color(color).next()
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

    val depthEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST)
    val depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)

    GL11.glDisable(GL11.GL_DEPTH_TEST)
    GL11.glDepthFunc(GL11.GL_ALWAYS)

    RenderSystem.lineWidth(3.5f)

    mc?.world?.let {
      val entities = (mc.world as ClientWorld).getEntities()

      val camera = mc.getBlockEntityRenderDispatcher().camera
      val cameraPos = camera.getPos()

      val camYaw = -toRadians(mc!!.player!!.getYaw(0.5f).toDouble())
      val camPitch = -toRadians(mc!!.player!!.getPitch(0.5f).toDouble())

      val dirVec = toDirVec(camPitch.toFloat(), camYaw.toFloat())

      if (mc.options.getBobView().getValue()) {
        wasBobblingOn = true

        mc.options.getBobView().setValue(false)
      }

      for (entity in entities) {
        if (entity == mc.player) continue

        val color = when {
          entity.isPlayer() && modPlayer.enabled() -> 0xFFB42828
          entity is LivingEntity && modMob.enabled() -> 0xFFB4B428
          modItem.enabled() && !(entity is LivingEntity) -> 0xFF96F0FF
          else -> continue
        }.toInt()

        val pos = entity.getLerpedPos(0.5f) ?: continue

        drawLine(
          stack, consumers,
          pos.add(0.0, entity.height / 2.0, 0.0).subtract(cameraPos),
          dirVec,
          color
        )
      }

      RenderSystem.setShader(GameRenderer::getPositionColorProgram)
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
    }

    if (depthEnabled) GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glDepthFunc(depthFunc)
  }
}
