package com.mck.longenchants;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Longs are stored in minecraft:custom_data, separate from the built-in
 * object-to-int enchantment map. This survives inventory saves and packet sync.
 */
public final class LongEnchantStorage {
    public static final String ROOT = "longenchants_levels_v1";

    private LongEnchantStorage() {}

    public static long get(ItemStack stack, Identifier enchantment) {
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null) return 0L;
        NbtCompound root = custom.copyNbt();
        return root.getCompoundOrEmpty(ROOT).getLong(enchantment.toString(), 0L);
    }

    public static void set(ItemStack stack, Identifier enchantment, long level) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root -> {
            NbtCompound levels = root.getCompoundOrEmpty(ROOT);
            if (level <= 0) {
                levels.remove(enchantment.toString());
            } else {
                levels.putLong(enchantment.toString(), level);
            }
            if (levels.isEmpty()) root.remove(ROOT);
            else root.put(ROOT, levels);
        });
    }
}
