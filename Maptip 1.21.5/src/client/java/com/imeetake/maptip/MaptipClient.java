package com.imeetake.maptip;

import com.imeetake.maptip.tooltip.MaptipTooltipComponent;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

public final class MaptipClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipComponentCallback.EVENT.register(MaptipClient::toComponent);
	}

	private static TooltipComponent toComponent(TooltipData data) {
		if (data instanceof MaptipTooltipData d) {
			return new MaptipTooltipComponent(d.stack());
		}
		return null;
	}
}