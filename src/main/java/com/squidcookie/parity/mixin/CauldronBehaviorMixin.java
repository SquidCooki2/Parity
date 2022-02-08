package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.cauldron.ModCauldronBehavior;
import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    // TODO Add stats for cauldron dyeing
    @Shadow @Final
    Map<Item, CauldronBehavior> WATER_CAULDRON_BEHAVIOR = null;

    /**
     * A new, better name for the target lambda method would most likely be, "REFACTOR_DYEABLE_ITEM", as the implementation for dyeing a dyeable item is now also found here
     */
    @Inject(at = @At("HEAD"), method = "method_32209(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    private static void cleanDyeableItemLambda(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        Item item = stack.getItem();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (item instanceof DyeableItem dyeableItem && blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && waterCauldronBlockEntity.hasColor() && dyeableItem.getColor(stack) != waterCauldronBlockEntity.getColor()) {
            if (!world.isClient) {
                dyeableItem.setColor(stack, waterCauldronBlockEntity.getColor());
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            infoReturnable.setReturnValue(ActionResult.success(world.isClient));
        } else if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && waterCauldronBlockEntity.hasColor()){
            infoReturnable.setReturnValue(ActionResult.PASS);
        }
    }

    /**
     * In conjunction with {@link #setBlockStateRedirect}, this method makes it so potions can be put into <strong>empty</strong> cauldrons
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;getPotion(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;"), method = "method_32222(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;")
    private static Potion getPotionRedirect(ItemStack stack) {
        return Potions.WATER;
    }

    /**
     * See {@link #getPotionRedirect}
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), method = "method_32222(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;")
    private static boolean setBlockStateRedirect(World world, BlockPos pos, BlockState state, BlockState stateArg, World worldArg, BlockPos posArg, PlayerEntity player, Hand hand, ItemStack stack) {
        if (PotionUtil.getPotion(stack) != Potions.WATER) {
            world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 0));
            WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
            waterCauldronBlockEntity.setPotion(stack);
        }
        return world.setBlockState(pos, state);
    }

    /**
     * Same signature as {@link #invoke$registerBehavior} without naming difference
     * <br>Clears water color if water bottle used at level == 3
     */
    @Inject(at = @At("HEAD"), method = "method_32219(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    private static void head$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        if (waterCauldronBlockEntity.hasColor() && state.get(LeveledCauldronBlock.LEVEL) == 3 && PotionUtil.getPotion(stack) == Potions.WATER) {
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                waterCauldronBlockEntity.removeColor();
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            infoReturnable.setReturnValue(ActionResult.success(world.isClient));
        }
    }

    /**
     * See {@link #head$registerBehavior}
     * <br>Clears water color if water bottle used at level != 3
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), method = "method_32219(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;")
    private static void invoke$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        waterCauldronBlockEntity.removeColor();
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/cauldron/CauldronBehavior;registerBehavior()V")
    private static void registerBehavior(CallbackInfo info) {
        WATER_CAULDRON_BEHAVIOR.put(Items.WHITE_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.ORANGE_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.MAGENTA_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_BLUE_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.YELLOW_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIME_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.PINK_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.GRAY_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_GRAY_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.CYAN_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.PURPLE_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLUE_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.BROWN_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.GREEN_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.RED_DYE, ModCauldronBehavior.DYE_CAULDRON);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLACK_DYE, ModCauldronBehavior.DYE_CAULDRON);
    }

    /**
     * Clears water color if a water bucket is used on colored cauldron
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), method = "Lnet/minecraft/block/cauldron/CauldronBehavior;fillCauldron(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/sound/SoundEvent;)Lnet/minecraft/util/ActionResult;")
    private static void fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, CallbackInfoReturnable<ActionResult> infoReturnable) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity) {
            waterCauldronBlockEntity.removeColor();
        }
    }
}
