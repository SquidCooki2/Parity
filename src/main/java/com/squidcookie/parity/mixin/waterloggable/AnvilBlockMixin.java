package com.squidcookie.parity.mixin.waterloggable;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin
extends FallingBlock
implements Waterloggable {
    private static final BooleanProperty WATERLOGGED;

    public AnvilBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/block/AnvilBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V")
    public void init(Settings settings, CallbackInfo info) {
        setDefaultState(this.stateManager.getDefaultState().with(AnvilBlock.FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/AnvilBlock;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;", cancellable = true)
    public void getPlacementState(ItemPlacementContext placementContext, CallbackInfoReturnable<BlockState> infoReturnable) {
        FluidState fluidState = placementContext.getWorld().getFluidState(placementContext.getBlockPos());
        infoReturnable.setReturnValue(this.getDefaultState().with(AnvilBlock.FACING, placementContext.getPlayerFacing().rotateYClockwise()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/block/AnvilBlock;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
    }
}
