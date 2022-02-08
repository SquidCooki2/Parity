package com.squidcookie.parity.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin
extends Block
implements Fertilizable {
    public SugarCaneBlockMixin(Settings settings) {
        super(settings);
    }

    private int getStalkLength(BlockView world, BlockPos pos) {
        int i = 1;
        while (world.getBlockState(pos.down(i)).isOf(this)) {
            i++;
        }
        return i;
    }

    private static boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        int length = this.getStalkLength(world, pos);
        BlockState blockState = world.getBlockState(pos.up());
        return length < 3 && !blockState.isOf(this) && canGrowInto(blockState);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int length = this.getStalkLength(world, pos);
        if (length < 3) {
            BlockPos blockPos = pos;
            for (int i = 0; i < 3 - length; i++) {
                blockPos = blockPos.up();
                world.setBlockState(blockPos, this.getDefaultState());
            }
        }
    }
}
