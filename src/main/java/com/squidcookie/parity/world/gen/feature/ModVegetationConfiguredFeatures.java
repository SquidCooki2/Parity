package com.squidcookie.parity.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class ModVegetationConfiguredFeatures {
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_BLUE_ORCHID;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_DANDELION;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_POPPY;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_ALLIUM;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_AZURE_BLUET;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_RED_TULIP;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_ORANGE_TULIP;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_WHITE_TULIP;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_PINK_TULIP;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_OXEYE_DAISY;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_CORNFLOWER;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_WITHER_ROSE;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_BONEMEAL_LILY_OF_THE_VALLEY;

    static {
        int tries = 12;
        int xzSpread = 3;
        int ySpread = 2;
        FLOWER_BONEMEAL_DANDELION = ConfiguredFeatures.register("flower_bonemeal_dandelion", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.DANDELION))).withInAirFilter())));
        FLOWER_BONEMEAL_POPPY = ConfiguredFeatures.register("flower_bonemeal_poppy", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.POPPY))).withInAirFilter())));
        FLOWER_BONEMEAL_BLUE_ORCHID = ConfiguredFeatures.register("flower_bonemeal_blue_orchid", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID))).withInAirFilter())));
        FLOWER_BONEMEAL_ALLIUM = ConfiguredFeatures.register("flower_bonemeal_allium", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ALLIUM))).withInAirFilter())));
        FLOWER_BONEMEAL_AZURE_BLUET = ConfiguredFeatures.register("flower_bonemeal_azure_bluet", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.AZURE_BLUET))).withInAirFilter())));
        FLOWER_BONEMEAL_RED_TULIP = ConfiguredFeatures.register("flower_bonemeal_red_tulip", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_TULIP))).withInAirFilter())));
        FLOWER_BONEMEAL_ORANGE_TULIP = ConfiguredFeatures.register("flower_bonemeal_orange_tulip", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ORANGE_TULIP))).withInAirFilter())));
        FLOWER_BONEMEAL_WHITE_TULIP = ConfiguredFeatures.register("flower_bonemeal_white_tulip", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.WHITE_TULIP))).withInAirFilter())));
        FLOWER_BONEMEAL_PINK_TULIP = ConfiguredFeatures.register("flower_bonemeal_pink_tulip", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PINK_TULIP))).withInAirFilter())));
        FLOWER_BONEMEAL_OXEYE_DAISY = ConfiguredFeatures.register("flower_bonemeal_oxeye_daisy", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.OXEYE_DAISY))).withInAirFilter())));
        FLOWER_BONEMEAL_CORNFLOWER = ConfiguredFeatures.register("flower_bonemeal_cornflower", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.CORNFLOWER))).withInAirFilter())));
        FLOWER_BONEMEAL_WITHER_ROSE = ConfiguredFeatures.register("flower_bonemeal_wither_rose", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.WITHER_ROSE))).withInAirFilter())));
        FLOWER_BONEMEAL_LILY_OF_THE_VALLEY = ConfiguredFeatures.register("flower_bonemeal_lily_of_the_valley", Feature.FLOWER.configure(new RandomPatchFeatureConfig(tries, xzSpread, ySpread, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY))).withInAirFilter())));
    }
}