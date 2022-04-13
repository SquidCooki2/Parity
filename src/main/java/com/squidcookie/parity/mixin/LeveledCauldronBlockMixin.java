package com.squidcookie.parity.mixin;

import com.squidcookie.parity.block.entity.WaterCauldronBlockEntity;
import com.squidcookie.parity.world.ModWorldEvents;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;
import java.util.Random;

@Mixin(LeveledCauldronBlock.class)
public abstract class LeveledCauldronBlockMixin
extends AbstractCauldronBlock
implements BlockEntityProvider {
    public LeveledCauldronBlockMixin(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaterCauldronBlockEntity waterCauldronBlockEntity && waterCauldronBlockEntity.hasPotion() && random.nextInt(10) == 0) {
            world.syncWorldEvent(ModWorldEvents.POTION_WATER_CAULDRON_PARTICLE_SPAWNED, pos, waterCauldronBlockEntity.getColor());
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WaterCauldronBlockEntity(pos, state);
    }
}
