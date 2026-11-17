# Long Enchants

Minecraft 1.21.11 / Fabric Loader 0.18.5 / Java 21.

Experimental mod to allow enchantments up to the signed 64-bit long limit, while formatting tooltip levels 1-10 in Roman numerals and levels 11+ with commas.

Usage: hold an item, then use `/longenchant minecraft:sharpness 9,223,372,036,854,775,807`.
Command requires operator permissions.

Levels at or below `Integer.MAX_VALUE` are mirrored into the vanilla enchantment component. Levels above this are stored exactly in `minecraft:custom_data`, while the vanilla component remains capped at `Integer.MAX_VALUE`, so gameplay effects do NOT scale fully to 64-bit range. Extreme values may cause vanilla calculation overflow.

This is experimental: compile and test in Minecraft before using it in an important world.