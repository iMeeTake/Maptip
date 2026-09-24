package com.imeetake.maptip.neoforge;

import com.imeetake.maptip.Maptip;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Maptip.MOD_ID)
public class NeoForgeMaptip {
    public NeoForgeMaptip(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(Maptip.MOD_ID, context, Maptip::initialize);
    }
}
