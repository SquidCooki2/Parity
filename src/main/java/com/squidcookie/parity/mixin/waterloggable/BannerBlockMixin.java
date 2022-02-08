package com.squidcookie.parity.mixin.waterloggable;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BannerBlock.class)
public class BannerBlockMixin
extends AbstractBannerBlock
implements Waterloggable {
    private static final BooleanProperty WATERLOGGED;

    protected BannerBlockMixin(DyeColor color, Settings settings) {
        super(color, settings);
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/block/BannerBlock;<init>(Lnet/minecraft/util/DyeColor;Lnet/minecraft/block/AbstractBlock$Settings;)V")
    public void init(DyeColor dyeColor, Settings settings, CallbackInfo info) {
        setDefaultState(this.stateManager.getDefaultState().with(BannerBlock.ROTATION, 0).with(WATERLOGGED, false));
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/BannerBlock;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;", cancellable = true)
    public void getPlacementState(ItemPlacementContext placementContext, CallbackInfoReturnable<BlockState> infoReturnable) {
        FluidState fluidState = placementContext.getWorld().getFluidState(placementContext.getBlockPos());
        infoReturnable.setReturnValue(this.getDefaultState().with(BannerBlock.ROTATION, MathHelper.floor((double)((180.0F + placementContext.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER));
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/BannerBlock;getStateForNeighborUpdate(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;")
    public void getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/block/BannerBlock;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V")
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
