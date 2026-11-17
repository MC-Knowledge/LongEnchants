package com.mck.longenchants.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Removes the Math.min(level, 255) caps in the builder's set/add paths. */
@Mixin(ItemEnchantmentsComponent.Builder.class)
public abstract class ItemEnchantmentsBuilderMixin {
    @ModifyConstant(method = {"set", "add"}, constant = @Constant(intValue = 255))
    private int longenchants$builderMaxLevel(int original) {
        return Integer.MAX_VALUE;
    }
}
