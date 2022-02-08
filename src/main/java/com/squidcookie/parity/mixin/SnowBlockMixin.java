package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.SnowLandingBlock;
import com.squidcookie.parity.entity.SnowFallingBlockEntity;
import net.minecraft.block.*;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SnowBlock.class)
public class SnowBlockMixin
extends Block
implements SnowLandingBlock {
    @Shadow @Final
    public static IntProperty LAYERS;

    public SnowBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.createAndScheduleBlockTick(pos, this, this.getFallDelay());
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/SnowBlock;getStateForNeighborUpdate(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", cancellable = true)
    public void getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> infoReturnable) {
        world.createAndScheduleBlockTick(pos, this, this.getFallDelay());
        infoReturnable.setReturnValue(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (SnowBlockMixin.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            SnowFallingBlockEntity snowFallingBlockEntity = new SnowFallingBlockEntity(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, world.getBlockState(pos));
            this.configureFallingBlockEntity(snowFallingBlockEntity);
            world.spawnEntity(snowFallingBlockEntity);
        }
    }

    protected void configureFallingBlockEntity(SnowFallingBlockEntity snowFallingBlockEntity) {
    }

    protected int getFallDelay() {
        return 2;
    }

    private static boolean canFallThrough(BlockState state) {
        Material material = state.getMaterial();
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.SNOW) && state.get(LAYERS) < 8 || material.isReplaceable() && !state.isOf(Blocks.SNOW);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((random.nextInt(16) == 0 && SnowBlockMixin.canFallThrough(world.getBlockState(pos.down()))) || (random.nextInt(16) == 0 && world.getBlockState(pos.down()).isIn(BlockTags.LEAVES))) {
            double x = (double)pos.getX() + random.nextDouble();
            double y = (double)pos.getY() - 0.05;
            double z = (double)pos.getZ() + random.nextDouble();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), x, y, z, 0.0, 0.0, 0.0);
        }
    }
}