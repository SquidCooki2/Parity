package com.squidcookie.parity.mixin;

import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO Make separate mod
@Mixin(CloudParticle.class)
public abstract class CloudParticleMixin
extends SpriteBillboardParticle {
    @Shadow @Final
    private SpriteProvider spriteProvider;

    protected CloudParticleMixin(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo info) {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        info.cancel();
    }
}
