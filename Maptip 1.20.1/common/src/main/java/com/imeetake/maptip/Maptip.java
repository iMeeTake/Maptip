package com.imeetake.maptip;

import com.imeetake.maptip.config.MaptipConfig;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.resources.ResourceLocation;

public class Maptip {
    public static final String MOD_ID = "maptip";

    public static void initialize() {
        Balm.getConfig().registerConfig(MaptipConfig.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
