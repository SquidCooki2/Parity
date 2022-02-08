package com.squidcookie.parity.block.entity;

import com.squidcookie.parity.Parity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ModBlockEntityType {
    public static BlockEntityType<WaterCauldronBlockEntity> WATER_CAULDRON;

    public static void registerBlockEntityTypes() {
        WATER_CAULDRON = Registry.register(Registry.BLOCK_ENTITY_TYPE, Parity.MOD_ID + ":water_cauldron", FabricBlockEntityTypeBuilder.create(WaterCauldronBlockEntity::new, Blocks.WATER_CAULDRON).build(null));
    }
}
