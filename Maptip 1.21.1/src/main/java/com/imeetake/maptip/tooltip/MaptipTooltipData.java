package com.imeetake.maptip.tooltip;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

public record MaptipTooltipData(ItemStack stack) implements TooltipData {}