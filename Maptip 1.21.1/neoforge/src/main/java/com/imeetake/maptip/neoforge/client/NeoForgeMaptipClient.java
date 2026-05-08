package com.imeetake.maptip.neoforge.client;

import com.imeetake.maptip.Maptip;
import com.imeetake.maptip.client.MaptipClient;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@Mod(value = Maptip.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeMaptipClient {
    public NeoForgeMaptipClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initializeMod(Maptip.MOD_ID, context, MaptipClient::initialize);
        modEventBus.addListener(NeoForgeMaptipClient::registerTooltipComponents);
    }

    private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(MaptipTooltipData.class, MaptipClient::createTooltipComponent);
    }
}
