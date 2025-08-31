package com.client.github.milkshake.mixin

import net.minecraft.entity.Entity

import com.client.github.feature.player.LiquidWalk
import com.client.github.feature.player.TargetStrafe

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Entity::class)
abstract class EntityMixin {
  @Inject(
    method = ["Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun _updateMovementInFluid(cir: CallbackInfoReturnable<Boolean>) = LiquidWalk.tick(this as Entity, cir)

  @Inject(
    method = ["Lnet/minecraft/entity/Entity;getJumpVelocityMultiplier()F"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  internal fun _getJumpVelocityMultiplier(cir: CallbackInfoReturnable<Float>) {
    if (TargetStrafe.enabled()) cir.setReturnValue(1f)
  }
}
