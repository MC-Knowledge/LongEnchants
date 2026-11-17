package com.mck.longenchants.mixin;

import com.mck.longenchants.LevelFormat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fixes untranslated enchantment.level.11+ names for any ordinary int-level enchantment. */
@Mixin(Enchantment.class)
public abstract class EnchantmentNameMixin {
    @Inject(method = "getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;",
            at = @At("HEAD"), cancellable = true)
    private static void longenchants$numericOverTen(
            RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<Text> cir) {
        if (level > 10) {
            cir.setReturnValue(enchantment.value().description().copy()
                .append(Text.literal(" " + LevelFormat.numeric(level)))
                .formatted(enchantment.isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.GRAY));
        }
    }
}
