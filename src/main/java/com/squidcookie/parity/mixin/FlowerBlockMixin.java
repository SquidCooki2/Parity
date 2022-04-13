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
        Block block = state.getBlock();

        if (block == Blocks.DANDELION) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_DANDELION.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.POPPY) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_POPPY.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.BLUE_ORCHID) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_BLUE_ORCHID.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.ALLIUM) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_ALLIUM.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.AZURE_BLUET) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_AZURE_BLUET.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.RED_TULIP) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_RED_TULIP.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.ORANGE_TULIP) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_ORANGE_TULIP.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.WHITE_TULIP) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_WHITE_TULIP.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.PINK_TULIP) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_PINK_TULIP.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.OXEYE_DAISY) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_OXEYE_DAISY.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.CORNFLOWER) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_CORNFLOWER.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.WITHER_ROSE) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_WITHER_ROSE.value().generate(world, chunkGenerator, random, pos);
        } else if (block == Blocks.LILY_OF_THE_VALLEY) {
            ModVegetationConfiguredFeatures.FLOWER_BONEMEAL_LILY_OF_THE_VALLEY.value().generate(world, chunkGenerator, random, pos);
        }
        // TODO add modded flower support
    }
}