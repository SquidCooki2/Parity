package com.squidcookie.parity.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin
extends Item {
    public BoneMealItemMixin(Settings settings) {
        super(settings);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOpaqueFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"), method = "createParticles")
    private static boolean isOpaqueFullCubeRedirect(BlockState state, BlockView world, BlockPos pos) {
        return state.isOpaqueFullCube(world, pos) || state.isIn(BlockTags.SMALL_FLOWERS);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"), method = "createParticles")
    private static BlockPos upRedirect(BlockPos pos, WorldAccess world) {
        if (!world.getBlockState(pos).isIn(BlockTags.SMALL_FLOWERS)) {
            return pos.up();
        }
        return pos;
    }

    /* TODO Config option to change the square "radius" (of the particles)!
    @ModifyVariable(at = @At("STORE"), method = "Lnet/minecraft/item/BoneMealItem;createParticles(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOpaqueFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;")), ordinal = 0)
    private static double dModify(double d) {
        return 2.0D;
    }
    */
}
