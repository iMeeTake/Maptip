package com.imeetake.maptip.client;

import com.imeetake.maptip.client.tooltip.MaptipTooltipComponent;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MaptipClient {
    public static void initialize(BalmClientRegistrars registrars) {
    }

    public static ClientTooltipComponent createTooltipComponent(MaptipTooltipData data) {
        return new MaptipTooltipComponent(data.stack());
    }
}
