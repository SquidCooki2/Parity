package com.squidcookie.parity.mixin;

import com.squidcookie.parity.mixin.invoke.LeveledCauldronBlockInvoker;
import com.squidcookie.parity.mixin.invoke.WorldRendererInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "processWorldEvent")
    public void processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo info) {
        Random random = world.random;
        BlockState blockState = world.getBlockState(pos);
        switch (eventId) {
            case 10000 -> {
                float r = (float)(data >> 16 & 0xFF) / 255.0f;
                float g = (float)(data >> 8 & 0xFF) / 255.0f;
                float b = (float)(data & 0xFF) / 255.0f;
                double origin = 3.0 / 16.0;
                double bound = 13.0 / 16.0;
                Particle particle = ((WorldRendererInvoker)this).spawnParticleInvoke(ParticleTypes.EFFECT, ParticleTypes.EFFECT.getType().shouldAlwaysSpawn(), pos.getX() + random.nextDouble(origin, bound), pos.getY() + ((LeveledCauldronBlockInvoker)blockState.getBlock()).getFluidHeightInvoke(blockState) + 1.0 / 16.0, pos.getZ() + random.nextDouble(origin, bound), 0.0, 0.0, 0.0);
                if (particle != null) {
                    float f = 0.75f + random.nextFloat() * 0.25f;
                    particle.setColor(r * f, g * f, b * f);
                }
            }
            case 10001 -> {
                // TODO Make custom Sound Event for this
                double origin = 3.75 / 16.0;
                double bound = 12.25 / 16.0;
                world.playSound(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int i = 0; i < 8; i++) {
                    this.world.addParticle(ParticleTypes.CLOUD, pos.getX() + random.nextDouble(origin, bound), pos.getY() + (6.0 + data * 3.0) / 16.0, pos.getZ() + random.nextDouble(origin, bound), 0.0, 0.0, 0.0);
                }
            }
            case 10002 -> {
                float r = (float)(data >> 16 & 0xFF) / 255.0f;
                float g = (float)(data >> 8 & 0xFF) / 255.0f;
                float b = (float)(data & 0xFF) / 255.0f;
                double origin = 0.4;
                double bound = 0.6;
                double fluidHeight = blockState.getBlock() instanceof LeveledCauldronBlock ? ((LeveledCauldronBlockInvoker)blockState.getBlock()).getFluidHeightInvoke(blockState.with(LeveledCauldronBlock.LEVEL, blockState.get(LeveledCauldronBlock.LEVEL) + 1)) + 1.0 / 16.0 : ((LeveledCauldronBlockInvoker)Blocks.WATER_CAULDRON).getFluidHeightInvoke(Blocks.WATER_CAULDRON.getDefaultState()) + 1.0 / 16.0;
                for (int i = 0; i < 3; i++) {
                    Particle particle = ((WorldRendererInvoker)this).spawnParticleInvoke(ParticleTypes.EFFECT, ParticleTypes.EFFECT.getType().shouldAlwaysSpawn(), pos.getX() + random.nextDouble(origin, bound), pos.getY() + fluidHeight, pos.getZ() + random.nextDouble(origin, bound), 0.0, random.nextGaussian() * 0.5, 0.0);
                    if (particle != null) {
                        float f = 0.75f + random.nextFloat() * 0.25f;
                        particle.setColor(r * f, g * f, b * f);
                    }
                }
            }
        }
    }
}
