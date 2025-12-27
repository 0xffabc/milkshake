package com.github

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.resource.ResourcePackActivationType

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.Window
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.Identifier

import com.client.github.feature.FeatureConfig
import com.client.github.feature.visual.*
import com.client.github.feature.elytra.*
import com.client.github.feature.combat.*
import com.client.github.feature.player.*
import com.client.github.bootstrap.Tick

import kotlin.math.*

infix fun Int.mod(mod: Int): Int = (this % mod + mod) % mod

val closeButton = KeyBinding(
  "Milkshake tabgui close",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_HOME,
  "net.minecraft.client.option",
)

val upArrowBind = KeyBinding(
  "Milkshake tabgui (next element)",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_DOWN,
  "net.minecraft.client.option"
)

val downArrowBind = KeyBinding(
  "Milkshake tabgui (previous element)",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_UP,
  "net.minecraft.client.option"
)

val tabKeyBind = KeyBinding(
  "Milkshake tabgui (flip flag)",
  InputUtil.Type.KEYSYM,
  InputUtil.GLFW_KEY_TAB,
  "net.minecraft.client.option"
)

object SagilithsPvEUtilsPrivateClient : ClientModInitializer {
  private lateinit var MC: MinecraftClient

  private lateinit var upKey: KeyBinding
  private lateinit var downKey: KeyBinding
  private lateinit var tabKey: KeyBinding
  private lateinit var closeKey: KeyBinding

  private var tabViewActive: Boolean = false
  private var tabIndex: Int = 0
  private var featureIndex: Int = 0

  private val width = 100
  private val x = 0
  private val y = 0

  const val COLOR_ENABLED_ACTIVE = 0xFFBAF636.toInt()
  const val COLOR_ACTIVE = 0xFFFFFFFF.toInt()
  const val COLOR_ENABLED = 0xFFA9F527.toInt()
  const val COLOR_DEFAULT = 0xFFE6E6E6.toInt()
  const val COLOR_BACKGROUND = 0x80000000.toInt()
  const val COLOR_BORDER = 0xFFFFFFFF.toInt()
  const val COLOR_HEADER = 0xFFAAAAAA.toInt()

  private val textLengths = mutableMapOf<String, Int>()

  private fun getTextWidth(text: String): Int {
    return textLengths.getOrPut(text) { textRenderer.getWidth(text) }
  }

  override fun onInitializeClient() {
    MC = MinecraftClient.getInstance()

    upKey = KeyBindingHelper.registerKeyBinding(upArrowBind)
    downKey = KeyBindingHelper.registerKeyBinding(downArrowBind)
    tabKey = KeyBindingHelper.registerKeyBinding(tabKeyBind)
    closeKey = KeyBindingHelper.registerKeyBinding(closeButton)

    HudRenderCallback.EVENT.register(::render)

    Tick.listen()

    ExtrasensoryPerception.bootstrap()
    ElytraTiming.bootstrap()
    ElytraFlight.bootstrap()
    Zoom.bootstrap()
    KillAura.bootstrap()
    AntiFireDamage.bootstrap()
    HoldHit.bootstrap()

    WorldRenderEvents.BEFORE_DEBUG_RENDER.register(::renderWorld)
  }

  private fun renderWorld(world: WorldRenderContext) {
    val stack = world?.matrixStack() ?: return
    val consumers = world?.consumers() ?: return

    ExtrasensoryPerception.render(stack, consumers)
  }

  private fun renderFeature(
    x: Int,
    y: Int,
    feature: String,
    context: DrawContext,
    active: Boolean
  ) {
    val enabled = FeatureConfig.config.getOrDefault(feature, false)

    val color = when {
      active && enabled -> COLOR_ENABLED_ACTIVE
      active -> COLOR_ACTIVE
      enabled -> COLOR_ENABLED
      else -> COLOR_DEFAULT
    }

    context.drawText(
      textRenderer,
      feature,
      x + (100 - getTextWidth(feature)) / 2,
      y + 5,
      color,
      true
    )
  }

  private fun renderContent(
    context: DrawContext,
    featureList: List<String>,
    header: String
  ): Boolean {
    if (tabKey!!.wasPressed()) {
      tabViewActive = !tabViewActive
    }

    val selectedFeature = featureList[featureIndex mod featureList.size]

    if (!tabViewActive && selectedFeature == "Back") {
      return false
    } else if (!tabViewActive) {
      FeatureConfig.config.put(selectedFeature, !FeatureConfig.config.getOrDefault(selectedFeature, false))

      tabViewActive = true
    }

    if (upKey!!.wasPressed()) featureIndex++
    if (downKey!!.wasPressed()) featureIndex--

    val contentHeight = offset * (featureList.size + 1) + 5

    context.fill(
      x, y, width, contentHeight,
      COLOR_BACKGROUND
    )

    context.drawBorder(
      x, y, width, contentHeight,
      COLOR_BORDER
    )

    context.drawText(
      textRenderer,
      header,
      (100 - getTextWidth(header)) / 2,
      y + 5,
      COLOR_HEADER,
      true
    )

    for ((index, feature) in featureList.withIndex()) {
      renderFeature(
        x,
        y + (index + 1) * offset,
        feature,
        context,
        index == featureIndex mod featureList.size
      )
    }

    return true
  }

  private fun renderHackList(
    window: Window,
    context: DrawContext,
    featureList: List<List<String>>
  ) {
    val featureList = featureList.flatten()
    val activeFeatures = featureList.filter { feature -> FeatureConfig.config.getOrDefault(feature, false) }

    if (activeFeatures.isEmpty()) {
      val text = "Legit, no hacks"
      context.drawText(
        textRenderer,
        text,
        window.scaledWidth - getTextWidth(text) - 5,
        5,
        COLOR_ENABLED,
        true
      )

      return
    }

    activeFeatures.forEachIndexed { index, feature ->
      context.drawText(
        textRenderer,
        feature,
        window.scaledWidth - getTextWidth(feature) - 5,
        index * offset,
        COLOR_DEFAULT,
        true
      )
    }
  }

  private val window: Window by lazy { MC.getWindow() }
  private val textRenderer: TextRenderer by lazy { MC.textRenderer }
  private val offset: Int by lazy { textRenderer.fontHeight + 5 }
  private val height: Int by lazy { (FeatureConfig.tabsData.size + 1) * offset }

  private var closed = false

  private fun render(
    context: DrawContext,
    tickCount: Float
  ) {
    if (closeKey!!.wasPressed()) {
      closed = !closed
    } else if (closed) return

    val tabsKeys = FeatureConfig.tabsData.keys.toList()
    val tabsValues = FeatureConfig.tabsData.values.toList()
    val currentTabIndex = tabIndex mod FeatureConfig.tabsData.size

    window?.let {
      renderHackList(window as Window, context, tabsValues)
    }

    if (FeatureConfig.tabsData.size == 0) return

    if (renderContent(
      context,
      tabsValues[currentTabIndex],
      tabsKeys[currentTabIndex]
    )) return

    featureIndex = 0

    context.fill(0, 0, 100, 100, COLOR_BACKGROUND)
    context.drawBorder(0, 0, 100, 100, COLOR_BORDER);

    context.drawText(
      textRenderer,
      "Milkshake",
      (100 - getTextWidth("Milkshake")) / 2,
      5,
      COLOR_HEADER,
      true
    )

    if (upKey?.wasPressed() ?: false) tabIndex++
    if (downKey?.wasPressed() ?: false) tabIndex--

    for ((index, value) in FeatureConfig.tabsData.entries.withIndex()) {
      val (group, featureList) = value
      val color = if (index == currentTabIndex) COLOR_ENABLED else COLOR_DEFAULT

      context.drawText(
        textRenderer,
        group,
        (100 - getTextWidth(group)) / 2,
        (index + 1) * offset + 5,
        color,
        false
      )
    }
  }
}
