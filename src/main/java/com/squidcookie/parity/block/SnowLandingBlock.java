package com.squidcookie.parity.block;

import com.squidcookie.parity.entity.SnowFallingBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SnowLandingBlock {
    default void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, SnowFallingBlockEntity snowFallingBlockEntity) {
    }

    default void onDestroyedOnLanding(World world, BlockPos pos, SnowFallingBlockEntity snowFallingBlockEntity) {
    }
}
