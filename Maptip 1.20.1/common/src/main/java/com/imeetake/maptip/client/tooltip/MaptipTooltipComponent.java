package com.imeetake.maptip.client.tooltip;

import com.imeetake.maptip.config.MaptipConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class MaptipTooltipComponent implements ClientTooltipComponent {
    private static final int BOTTOM_PADDING = 6;

    private final ItemStack stack;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(Font font) {
        return MaptipConfig.getPreviewSize();
    }

    @Override
    public int getHeight() {
        return MaptipConfig.getPreviewSize() + BOTTOM_PADDING;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        int sizePx = MaptipConfig.getPreviewSize();
        MaptipPreviewRenderer.renderFrame(guiGraphics, x, y, sizePx);

        Integer mapId = MapItem.getMapId(stack);
        if (mapId == null) {
            return;
        }

        MapItemSavedData mapData = MapItem.getSavedData(mapId, client.level);
        if (mapData == null) {
            return;
        }

        MapRenderer renderer = client.gameRenderer.getMapRenderer();
        MaptipPreviewRenderer.renderMap(guiGraphics, x, y, sizePx, renderer, mapId, mapData);
    }
}
