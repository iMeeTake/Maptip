package com.imeetake.maptip.fabric;

import com.imeetake.maptip.Maptip;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;

public class FabricMaptip implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(Maptip.MOD_ID, FabricLoadContext.INSTANCE, Maptip::initialize);
    }
}
