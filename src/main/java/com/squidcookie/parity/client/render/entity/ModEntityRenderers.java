package com.squidcookie.parity.client.render.entity;

import com.squidcookie.parity.entity.ModEntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ModEntityRenderers {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntityType.SNOW_FALLING_BLOCK, SnowFallingBlockEntityRenderer::new);
    }
}
