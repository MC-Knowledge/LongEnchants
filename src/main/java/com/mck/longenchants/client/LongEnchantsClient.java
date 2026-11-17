package com.mck.longenchants.client;

import com.mck.longenchants.LevelFormat;
import com.mck.longenchants.LongEnchantStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/** Replaces the mirrored tooltip with its true 64-bit level on the client. */
public final class LongEnchantsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> replaceLongLevels(stack, lines));
    }

    private static void replaceLongLevels(ItemStack stack, List<Text> lines) {
        replaceForComponent(stack, lines,
            stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT));
        replaceForComponent(stack, lines,
            stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT));
    }

    private static void replaceForComponent(ItemStack stack, List<Text> lines, ItemEnchantmentsComponent nativeEnchantments) {
        for (var entry : nativeEnchantments.getEnchantmentEntries()) {
            RegistryEntry<Enchantment> enchantment = entry.getKey();
            var key = enchantment.getKey();
            if (key.isEmpty()) continue;
            long actualLevel = LongEnchantStorage.get(stack, key.get().getValue());
            if (actualLevel <= Integer.MAX_VALUE) continue;

            String nativeLine = Enchantment.getName(enchantment, entry.getIntValue()).getString();
            Text replacement = enchantment.value().description().copy()
                .append(Text.literal(" " + LevelFormat.numeric(actualLevel)))
                .formatted(enchantment.isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.GRAY);

            boolean replaced = false;
            for (int i = 1; i < lines.size(); i++) { // never replace the actual item's name
                if (lines.get(i).getString().equals(nativeLine)) {
                    lines.set(i, replacement);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                // Do not fabricate a missing line if enchantments are hidden by tooltip settings.
                // Keeping the value in custom_data still preserves the level.
            }
        }
    }
}
