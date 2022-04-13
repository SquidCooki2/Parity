package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.cauldron.ModCauldronBehavior;
import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import com.squidcookie.parity.stat.ModStats;
import com.squidcookie.parity.world.ModWorldEvents;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BannerBlockEntity;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Shadow @Final
    Map<Item, CauldronBehavior> WATER_CAULDRON_BEHAVIOR = null;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getBlockFromItem(Lnet/minecraft/item/Item;)Lnet/minecraft/block/Block;"), method = "method_32215")
    private static Block getBlockFromItemRedirect(Item item, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        return waterCauldronBlockEntity.hasColor() || !(Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) ? Blocks.AIR : Blocks.SHULKER_BOX;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LeveledCauldronBlock;decrementFluidLevel(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER), method = "method_32215")
    private static void CleanShulkerBoxLambda(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir) {
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BannerBlockEntity;getPatternCount(Lnet/minecraft/item/ItemStack;)I"), method = "method_32214")
    private static int getPatternCountRedirect(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stackArg) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        return waterCauldronBlockEntity.hasColor() || BannerBlockEntity.getPatternCount(stack) <= 0 ? 0 : 1;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LeveledCauldronBlock;decrementFluidLevel(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER), method = "method_32214")
    private static void CleanBannerLambda(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir) {
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    /**
     * A new, better name for the target-lambda method would most likely be, "REFACTOR_DYEABLE_ITEM", as
     * the implementation for dyeing a dyeable item is now also found here
     */
    @Inject(at = @At("HEAD"), method = "method_32209", cancellable = true)
    private static void head$cleanDyeableItemLambda(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        Item item = stack.getItem();
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        if (item instanceof DyeableItem dyeableItem && !waterCauldronBlockEntity.hasPotion() && waterCauldronBlockEntity.hasColor() && dyeableItem.getColor(stack) != waterCauldronBlockEntity.getColor()) {
            if (!world.isClient) {
                dyeableItem.setColor(stack, waterCauldronBlockEntity.getColor());
                player.incrementStat(ModStats.DYE_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            infoReturnable.setReturnValue(ActionResult.success(world.isClient));
        } else if (waterCauldronBlockEntity.hasColor()) {
            infoReturnable.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LeveledCauldronBlock;decrementFluidLevel(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER), method = "method_32209")
    private static void invoke$cleanDyeableItemLambda(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    // Empty -----------------------------

    /**
     * This method essentially disables the if-statement at the head of the method
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;getPotion(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;"), method = "method_32222")
    private static Potion empty$getPotionRedirect(ItemStack stack) {
        return Potions.WATER;
    }

    /**
     * Sets potion for <strong>empty</strong> cauldrons
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), method = "method_32222")
    private static void invoke$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        Potion potion = PotionUtil.getPotion(stack);
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        waterCauldronBlockEntity.setPotion(potion);
        if (potion != Potions.WATER) {
            world.syncWorldEvent(ModWorldEvents.POTION_WATER_CAULDRON_FILLED, pos, waterCauldronBlockEntity.getColor());
        }
    }

    // Water -----------------------------

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;setPotion(Lnet/minecraft/item/ItemStack;Lnet/minecraft/potion/Potion;)Lnet/minecraft/item/ItemStack;"), method = "method_32220")
    private static void setPotionModifyArgs(Args args, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        args.set(1, Potion.byId(waterCauldronBlockEntity.getPotion()));
    }

    /**
     * In conjunction with {@link #getRedirect}, this method essentially disables the filtering if-statement at the head of the method
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;getPotion(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;"), method = "method_32219")
    private static Potion water$getPotionRedirect(ItemStack stack) {
        return Potions.WATER;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"), method = "method_32219")
    private static Comparable getRedirect(BlockState state, Property property) {
        return -1;
    }

    /**
     * Sizzling when attempting to put mismatched potions in the same cauldron
     */
    @Inject(at = @At("HEAD"), method = "method_32219", cancellable = true)
    private static void head$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        Potion potion = PotionUtil.getPotion(stack);
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        if (!waterCauldronBlockEntity.getPotion().equals(Registry.POTION.getId(potion).toString())) {
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                int level = state.get(LeveledCauldronBlock.LEVEL);
                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                world.syncWorldEvent(ModWorldEvents.WATER_CAULDRON_SIZZLE, pos, level);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            infoReturnable.setReturnValue(ActionResult.success(world.isClient));
        }
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "method_32219", cancellable = true)
    private static void field$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        if (state.get(LeveledCauldronBlock.LEVEL) == 3 && (waterCauldronBlockEntity.hasPotion() || !waterCauldronBlockEntity.hasColor())) {
            infoReturnable.setReturnValue(ActionResult.PASS);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), method = "method_32219")
    private static boolean setBlockStateRedirect(World world, BlockPos pos, BlockState state, BlockState stateArg) {
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        // noinspection SimplifiableConditionalExpression
        return !waterCauldronBlockEntity.hasPotion() && stateArg.get(LeveledCauldronBlock.LEVEL) == 3 ? false : world.setBlockState(pos, state);
    }

    /**
     * Sets potions and clears color for <strong>water</strong> cauldrons
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), method = "method_32219", cancellable = true)
    private static void invoke2$registerBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> infoReturnable) {
        Potion potion = PotionUtil.getPotion(stack);
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        waterCauldronBlockEntity.setPotion(potion);
        if (potion != Potions.WATER) {
            world.syncWorldEvent(ModWorldEvents.POTION_WATER_CAULDRON_FILLED, pos, waterCauldronBlockEntity.getColor());
        }
    }

    // -----------------------------------

    @Inject(at = @At("HEAD"), method = "registerBehavior")
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

    @Inject(at = @At("HEAD"), method = "fillCauldron", cancellable = true)
    private static void head$fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, CallbackInfoReturnable<ActionResult> infoReturnable) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && waterCauldronBlockEntity.hasPotion() && stack.getItem() == Items.WATER_BUCKET) {
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                int level = world.getBlockState(pos).get(LeveledCauldronBlock.LEVEL);
                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                world.syncWorldEvent(ModWorldEvents.WATER_CAULDRON_SIZZLE, pos, level);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            infoReturnable.setReturnValue(ActionResult.success(world.isClient));
        }
    }

    /**
     * Clears water color if a water bucket is used on a colored cauldron
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), method = "fillCauldron")
    private static void invoke$fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, CallbackInfoReturnable<ActionResult> infoReturnable) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && stack.getItem() == Items.WATER_BUCKET) {
            waterCauldronBlockEntity.setPotion(Potions.WATER);
        }
    }
}