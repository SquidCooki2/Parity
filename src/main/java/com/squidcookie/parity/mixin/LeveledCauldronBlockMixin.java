package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(LeveledCauldronBlock.class)
public class LeveledCauldronBlockMixin
extends AbstractCauldronBlock
implements BlockEntityProvider {
    public LeveledCauldronBlockMixin(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
    }

    @Override
    public boolean isFull(BlockState state) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WaterCauldronBlockEntity(pos, state);
    }
}
