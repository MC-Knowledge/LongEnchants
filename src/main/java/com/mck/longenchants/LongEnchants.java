package com.mck.longenchants;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class LongEnchants implements ModInitializer {
    /** Vanilla effects use int, while custom_data retains the full long value. */
    public static final int VANILLA_MIRROR_CAP = Integer.MAX_VALUE;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("longenchant")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(argument("enchantment", StringArgumentType.word())
                    .then(argument("level", StringArgumentType.greedyString())
                        .executes(ctx -> apply(
                            ctx.getSource(),
                            StringArgumentType.getString(ctx, "enchantment"),
                            StringArgumentType.getString(ctx, "level")
                        ))
                    )
                )
            )
        );
    }

    private static int apply(ServerCommandSource source, String rawId, String rawLevel) {
        var player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("Run this command as a player holding an item."));
            return 0;
        }
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            source.sendError(Text.literal("Hold an item in your main hand."));
            return 0;
        }
        Identifier id = Identifier.tryParse(rawId.contains(":") ? rawId : "minecraft:" + rawId);
        if (id == null) {
            source.sendError(Text.literal("Invalid enchantment ID: " + rawId));
            return 0;
        }
        long level;
        try {
            level = LevelFormat.parseLevel(rawLevel);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("Invalid level: " + e.getMessage()));
            return 0;
        }

        Registry<Enchantment> registry = source.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        var optional = registry.getEntry(id);
        if (optional.isEmpty()) {
            source.sendError(Text.literal("Unknown enchantment: " + id));
            return 0;
        }
        RegistryEntry<Enchantment> enchantment = optional.get();
        boolean isBook = stack.isOf(Items.ENCHANTED_BOOK);
        var componentType = isBook ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS;
        var current = stack.getOrDefault(componentType, ItemEnchantmentsComponent.DEFAULT);
        var builder = new ItemEnchantmentsComponent.Builder(current);
        if (level == 0L) {
            builder.remove(entry -> entry.equals(enchantment));
        } else {
            builder.set(enchantment, (int) Math.min(level, VANILLA_MIRROR_CAP));
        }
        stack.set(componentType, builder.build());
        LongEnchantStorage.set(stack, id, level);

        final String message = level == 0L
            ? "Removed " + id + " from held item"
            : "Set " + id + " to " + LevelFormat.numeric(level)
                + " (vanilla effective level capped at " + VANILLA_MIRROR_CAP + ")";
        source.sendFeedback(() -> Text.literal(message), false);
        return 1;
    }
}
