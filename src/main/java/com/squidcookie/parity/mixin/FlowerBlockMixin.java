package com.squidcookie.parity.mixin;

import com.squidcookie.parity.world.gen.feature.ModVegetationConfiguredFeatures;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin
extends PlantBlock
implements Fertilizable {
    protected FlowerBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        if (state.isOf(Blocks.DANDELION)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_DANDELION.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.POPPY)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_POPPY.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.BLUE_ORCHID)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_BLUE_ORCHID.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.ALLIUM)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_ALLIUM.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.AZURE_BLUET)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_AZURE_BLUET.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.RED_TULIP)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_RED_TULIP.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.ORANGE_TULIP)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_ORANGE_TULIP.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.WHITE_TULIP)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_WHITE_TULIP.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.PINK_TULIP)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_PINK_TULIP.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.OXEYE_DAISY)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_OXEYE_DAISY.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.CORNFLOWER)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_CORNFLOWER.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.WITHER_ROSE)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_WITHER_ROSE.generate(world, chunkGenerator, random, pos);
        } else if (state.isOf(Blocks.LILY_OF_THE_VALLEY)) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_LILY_OF_THE_VALLEY.generate(world, chunkGenerator, random, pos);
        }
        // TODO add modded flower support
    }
}