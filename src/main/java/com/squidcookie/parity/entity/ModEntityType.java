package com.squidcookie.parity.entity;

import com.squidcookie.parity.Parity;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.registry.Registry;

public class ModEntityType<T extends Entity> {
    public static final EntityType<SnowFallingBlockEntity> SNOW_FALLING_BLOCK = Registry.register(Registry.ENTITY_TYPE, new Identifier(Parity.MOD_ID, "snow_falling_block"), EntityType.Builder.<SnowFallingBlockEntity>create(SnowFallingBlockEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(20).build("snow_falling_block"));
}