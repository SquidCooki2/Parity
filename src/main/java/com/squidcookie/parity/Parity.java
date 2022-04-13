package com.squidcookie.parity;

import com.squidcookie.parity.block.entity.ModBlockEntityType;
import com.squidcookie.parity.stat.ModStats;
import com.squidcookie.parity.world.gen.feature.ModVegetationConfiguredFeatures;
import net.fabricmc.api.ModInitializer;

public class Parity
implements ModInitializer {
    public static final String MOD_ID = "parity";

    @Override
    public void onInitialize() {
        ModBlockEntityType.registerBlockEntityTypes();
        ModStats.registerStats();
        ModVegetationConfiguredFeatures.registerVegetationConfiguredFeatures();
    }
}
