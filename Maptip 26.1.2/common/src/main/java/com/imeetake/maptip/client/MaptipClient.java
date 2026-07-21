package com.imeetake.maptip.client;

import com.imeetake.maptip.client.tooltip.MaptipTooltipComponent;
import com.imeetake.maptip.config.MaptipConfig;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MaptipClient {
    public static void initialize(BalmClientRegistrars registrars) {
    }

    public static ClientTooltipComponent createTooltipComponent(MaptipTooltipData data) {
        return new MaptipTooltipComponent(data.stack());
    }

    public static boolean shouldShowPreview() {
        if (!MaptipConfig.getRequireShift()) {
            return true;
        }

        Minecraft client = Minecraft.getInstance();
        return InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_LSHIFT)
            || InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_RSHIFT);
    }
}
