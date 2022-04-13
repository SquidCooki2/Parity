package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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
    @Inject(at = @At("HEAD"), method = "getWaterColor", cancellable = true)
    private static void getWaterColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> infoReturnable) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity) {
            if (waterCauldronBlockEntity.hasColor()) {
                infoReturnable.setReturnValue(waterCauldronBlockEntity.getColor());
            }

            // Quick little fix to the Bedrock bug where a potion cauldron appears to be a water cauldron for a split second
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            ItemStack stack;
            if (player.isHolding(Items.POTION) && waterCauldronBlockEntity.isEmptyPotion()) {
                if (player.getMainHandStack().getItem() == Items.POTION) {
                    stack = player.getMainHandStack();
                } else {
                    stack = player.getOffHandStack();
                }
                if (PotionUtil.getPotion(stack) != Potions.WATER) {
                    infoReturnable.setReturnValue(PotionUtil.getColor(stack));
                }
            }
        }
    }
}