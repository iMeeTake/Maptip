package com.imeetake.maptip.neoforge.client;

import com.imeetake.maptip.Maptip;
import com.imeetake.maptip.client.MaptipClient;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Maptip.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeMaptipClient {
    public NeoForgeMaptipClient(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        BalmClient.initializeMod(Maptip.MOD_ID, context, MaptipClient::initialize);
        modEventBus.addListener(this::registerTooltipComponents);
    }

    private void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(MaptipTooltipData.class, MaptipClient::createTooltipComponent);
    }
}
