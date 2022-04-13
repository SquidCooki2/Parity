package com.squidcookie.parity.mixin.invoke;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LeveledCauldronBlock.class)
public interface LeveledCauldronBlockInvoker {
    @Invoker("getFluidHeight")
    double getFluidHeightInvoke(BlockState state);
}
