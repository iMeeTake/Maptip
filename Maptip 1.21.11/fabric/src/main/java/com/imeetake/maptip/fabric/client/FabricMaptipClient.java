package com.imeetake.maptip.fabric.client;

import com.imeetake.maptip.Maptip;
import com.imeetake.maptip.client.MaptipClient;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.api.ClientModInitializer;

public class FabricMaptipClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(Maptip.MOD_ID, FabricLoadContext.INSTANCE, MaptipClient::initialize);
        TooltipComponentCallback.EVENT.register(data -> data instanceof MaptipTooltipData maptipData ? MaptipClient.createTooltipComponent(maptipData) : null);
    }
}
