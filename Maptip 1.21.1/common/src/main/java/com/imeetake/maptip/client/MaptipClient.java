package com.imeetake.maptip.client;

import com.imeetake.maptip.client.tooltip.MaptipTooltipComponent;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MaptipClient {
    public static void initialize() {
    }

    public static ClientTooltipComponent createTooltipComponent(MaptipTooltipData data) {
        return new MaptipTooltipComponent(data.stack());
    }
}
