package com.squidcookie.parity.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class ModVegetationConfiguredFeatures {
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_BLUE_ORCHID;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_DANDELION;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_POPPY;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_ALLIUM;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_AZURE_BLUET;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_RED_TULIP;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_ORANGE_TULIP;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_WHITE_TULIP;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_PINK_TULIP;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_OXEYE_DAISY;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_CORNFLOWER;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_WITHER_ROSE;
    public static RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_BONEMEAL_LILY_OF_THE_VALLEY;
    private static final int tries = 12;
    private static final int xzSpread = 3;
    private static final int ySpread = 2;

    public static void registerVegetationConfiguredFeatures() {
        FLOWER_BONEMEAL_DANDELION = ConfiguredFeatures.register("flower_bonemeal_dandelion", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.DANDELION)))));
        FLOWER_BONEMEAL_POPPY = ConfiguredFeatures.register("flower_bonemeal_poppy", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.POPPY)))));
        FLOWER_BONEMEAL_BLUE_ORCHID = ConfiguredFeatures.register("flower_bonemeal_blue_orchid", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID)))));
        FLOWER_BONEMEAL_ALLIUM = ConfiguredFeatures.register("flower_bonemeal_allium", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ALLIUM)))));
        FLOWER_BONEMEAL_AZURE_BLUET = ConfiguredFeatures.register("flower_bonemeal_azure_bluet", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.AZURE_BLUET)))));
        FLOWER_BONEMEAL_RED_TULIP = ConfiguredFeatures.register("flower_bonemeal_red_tulip", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_TULIP)))));
        FLOWER_BONEMEAL_ORANGE_TULIP = ConfiguredFeatures.register("flower_bonemeal_orange_tulip", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ORANGE_TULIP)))));
        FLOWER_BONEMEAL_WHITE_TULIP = ConfiguredFeatures.register("flower_bonemeal_white_tulip", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.WHITE_TULIP)))));
        FLOWER_BONEMEAL_PINK_TULIP = ConfiguredFeatures.register("flower_bonemeal_pink_tulip", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PINK_TULIP)))));
        FLOWER_BONEMEAL_OXEYE_DAISY = ConfiguredFeatures.register("flower_bonemeal_oxeye_daisy", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.OXEYE_DAISY)))));
        FLOWER_BONEMEAL_CORNFLOWER = ConfiguredFeatures.register("flower_bonemeal_cornflower", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.CORNFLOWER)))));
        FLOWER_BONEMEAL_WITHER_ROSE = ConfiguredFeatures.register("flower_bonemeal_wither_rose", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.WITHER_ROSE)))));
        FLOWER_BONEMEAL_LILY_OF_THE_VALLEY = ConfiguredFeatures.register("flower_bonemeal_lily_of_the_valley", Feature.FLOWER, new RandomPatchFeatureConfig(tries, xzSpread, ySpread, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))));
    }
}