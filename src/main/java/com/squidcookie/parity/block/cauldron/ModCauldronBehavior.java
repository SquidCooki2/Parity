package com.squidcookie.parity.block.cauldron;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import com.squidcookie.parity.stat.ModStats;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

public interface ModCauldronBehavior {
    CauldronBehavior DYE_CAULDRON = (state, world, pos, player, hand, stack) -> {
        Item item = stack.getItem();
        WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
        if (!(item instanceof DyeItem) || waterCauldronBlockEntity.hasPotion()) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                player.incrementStat(ModStats.DYE_CAULDRON);
                waterCauldronBlockEntity.addColor((DyeItem)item);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }
    };
}
