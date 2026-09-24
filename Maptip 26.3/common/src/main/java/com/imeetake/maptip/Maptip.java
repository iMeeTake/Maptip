package com.imeetake.maptip;

import com.imeetake.maptip.config.MaptipConfig;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.resources.Identifier;

public class Maptip {
    public static final String MOD_ID = "maptip";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void initialize(BalmRegistrars registrars) {
        Balm.config().registerConfig(MaptipConfig.class);
    }
}
