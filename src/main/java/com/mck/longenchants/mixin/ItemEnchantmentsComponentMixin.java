package com.mck.longenchants.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * The component constructor rejects levels over 255, while the codec uses
 * Codec.intRange(1, 255). Retarget both guards to Integer.MAX_VALUE.
 */
@Mixin(ItemEnchantmentsComponent.class)
public abstract class ItemEnchantmentsComponentMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 255))
    private int longenchants$constructorMaxLevel(int original) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 255))
    private static int longenchants$codecMaxLevel(int original) {
        return Integer.MAX_VALUE;
    }
}
