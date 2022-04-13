package com.squidcookie.parity.stat;

import com.squidcookie.parity.Parity;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModStats {
    public static Identifier DYE_CAULDRON;
    public static Identifier DYE_ARMOR;

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = new Identifier(Parity.MOD_ID, id);
        Registry.register(Registry.CUSTOM_STAT, identifier, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }

    public static void registerStats() {
        DYE_CAULDRON = register("dye_cauldron", StatFormatter.DEFAULT);
        DYE_ARMOR = register("dye_armor", StatFormatter.DEFAULT);
    }
}
