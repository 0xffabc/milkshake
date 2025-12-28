package com.client.github.milkshake.mixin

import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.random.Random
import net.minecraft.block.Blocks
import net.minecraft.util.math.Box
import net.minecraft.client.render.WorldRenderer

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

import com.client.github.feature.Module

@Mixin(BlockRenderManager::class)
abstract class BlockRenderManagerMixin {
    @Unique
    private val mod = Module("Visual", "Xray")

    fun ARGBToFloats(color: Long): FloatArray {
        return floatArrayOf(
            (color shr 16 and 0xFF) / 255f,
            (color shr 8 and 0xFF) / 255f,
            (color and 0xFF) / 255f,
            (color shr 24 and 0xFF) / 255f
        )
    }

    @Inject(
        method = ["method_3355(Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_1920;Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;ZLnet/minecraft/class_5819;)V"],
        at = [At("HEAD")]
    )
    private fun renderBlock(
        state: BlockState,
        pos: BlockPos,
        world: BlockRenderView,
        matrices: MatrixStack,
        vertexConsumer: VertexConsumer,
        cull: Boolean,
        random: Random,
        ci: CallbackInfo
    ) {
        if (mod.disabled()) return

        println("BlockRenderManagerMixin")

        if (state != Blocks.COAL_ORE.defaultState &&
            state != Blocks.COPPER_ORE.defaultState &&
            state != Blocks.DEEPSLATE_COAL_ORE.defaultState &&
            state != Blocks.DEEPSLATE_DIAMOND_ORE.defaultState &&
            state != Blocks.DEEPSLATE_EMERALD_ORE.defaultState &&
            state != Blocks.DEEPSLATE_GOLD_ORE.defaultState &&
            state != Blocks.DEEPSLATE_IRON_ORE.defaultState &&
            state != Blocks.DEEPSLATE_LAPIS_ORE.defaultState &&
            state != Blocks.DEEPSLATE_REDSTONE_ORE.defaultState &&
            state != Blocks.DIAMOND_ORE.defaultState &&
            state != Blocks.EMERALD_ORE.defaultState &&
            state != Blocks.GOLD_ORE.defaultState &&
            state != Blocks.IRON_ORE.defaultState &&
            state != Blocks.LAPIS_ORE.defaultState &&
            state != Blocks.REDSTONE_ORE.defaultState
        ) return



        val boxColor = when (state.block) {
            Blocks.COAL_ORE -> 0xFF212121
            Blocks.COPPER_ORE -> 0xFFAB6A2E
            Blocks.DEEPSLATE_COAL_ORE -> 0xFF000000
            Blocks.DEEPSLATE_DIAMOND_ORE -> 0xFF5EC0C7
            Blocks.DEEPSLATE_EMERALD_ORE -> 0xFF67C75D
            Blocks.DEEPSLATE_GOLD_ORE -> 0xFFC2C75D
            Blocks.DEEPSLATE_IRON_ORE -> 0xFFF1EBE4
            Blocks.DEEPSLATE_LAPIS_ORE -> 0xFF4C4CBA
            Blocks.DEEPSLATE_REDSTONE_ORE -> 0xFFBA4C4C
            Blocks.DIAMOND_ORE -> 0xFF4CBAAC
            Blocks.EMERALD_ORE -> 0xFF62BA4C
            Blocks.GOLD_ORE -> 0xFFB9BA4C
            Blocks.IRON_ORE -> 0xFFE3D6D5
            Blocks.LAPIS_ORE -> 0xFF3E3EBF
            Blocks.REDSTONE_ORE -> 0xFFBF3D3D
            else -> 0xFF000000
        }

        val box = Box(pos)

        val floats = ARGBToFloats(boxColor)

        WorldRenderer.drawBox(matrices, vertexConsumer, box, floats[0], floats[1], floats[2], floats[3])
    }
}
