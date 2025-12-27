package com.client.github.milkshake.mixin

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.util.Identifier

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

import com.client.github.utility.CustomCape

@Mixin(AbstractClientPlayerEntity::class)
abstract class AbstractClientPlayerEntityMixin {
   @Overwrite
   fun method_3125(): Boolean {
       return method_3119() != null
   }

    @Overwrite
    fun method_3119(): Identifier? {
        val cape = CustomCape.getByPlayerName((this as AbstractClientPlayerEntity).getDisplayName().asTruncatedString(30), this as AbstractClientPlayerEntity)

        return cape
    }
}
