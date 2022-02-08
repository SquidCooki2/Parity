package com.squidcookie.parity.mixin;

import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Properties.class)
public class PropertiesMixin {
    /**
     * Makes it possible to store color data before cauldron has water. Useful for making the transition from an empty cauldron to a colored cauldron a smoother one. Does not affect gameplay or other mods
     */
    @Shadow @Final
    public static final IntProperty LEVEL_3 = IntProperty.of("level", 0, 3);
}
