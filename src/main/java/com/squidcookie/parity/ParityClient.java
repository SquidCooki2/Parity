package com.squidcookie.parity;

import com.squidcookie.parity.client.render.entity.ModEntityRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParityClient
implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityRenderers.registerEntityRenderers();
    }
}
