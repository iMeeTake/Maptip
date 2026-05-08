package com.imeetake.maptip.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record MaptipTooltipData(ItemStack stack) implements TooltipComponent {
}
