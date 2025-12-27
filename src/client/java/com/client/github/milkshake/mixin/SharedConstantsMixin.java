package com.client.github.milkshake.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    /**
     * @author Pulsar
     * @reason Because I wanted to
    **/
    @Overwrite
    public static void enableDataFixerOptimization() { }
}
