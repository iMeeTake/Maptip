package com.imeetake.maptip;

import net.minecraft.resources.ResourceLocation;

public class Maptip {
    public static final String MOD_ID = "maptip";

    public static void initialize() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
