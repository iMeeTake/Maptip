package com.imeetake.maptip.fabric;

import com.imeetake.maptip.Maptip;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.fabricmc.api.ModInitializer;

public class FabricMaptip implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(Maptip.MOD_ID, EmptyLoadContext.INSTANCE, Maptip::initialize);
    }
}
