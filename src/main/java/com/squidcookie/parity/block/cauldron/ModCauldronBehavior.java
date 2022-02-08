package com.squidcookie.parity.block.cauldron;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;

public interface ModCauldronBehavior {
    // TODO Add stats for cauldron dyeing
    CauldronBehavior DYE_CAULDRON = (state, world, pos, player, hand, stack) -> {
        Item item = stack.getItem();
        if (!(item instanceof DyeItem)) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                WaterCauldronBlockEntity waterCauldronBlockEntity = (WaterCauldronBlockEntity)world.getBlockEntity(pos);
                waterCauldronBlockEntity.addColor((DyeItem)item);
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }
    };
}
