package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {
    /**
     * Tints water cauldrons by getting {@link WaterCauldronBlockEntity#getColor()}
     */
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/color/world/BiomeColors;getWaterColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I", cancellable = true)
    private static void getWaterColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> infoReturnable) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && waterCauldronBlockEntity.hasColor()) {
            infoReturnable.setReturnValue(waterCauldronBlockEntity.getColor());
        }
    }
}
