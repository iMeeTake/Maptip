package com.imeetake.maptip.client.tooltip;

import com.imeetake.maptip.config.MaptipConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
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
    public int getHeight(Font font) {
        return MaptipConfig.getPreviewSize() + BOTTOM_PADDING;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        int sizePx = MaptipConfig.getPreviewSize();
        MaptipPreviewRenderer.extractFrame(graphics, x, y, sizePx);

        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (mapId == null) {
            return;
        }

        MapItemSavedData mapData = MapItem.getSavedData(mapId, client.level);
        if (mapData == null) {
            return;
        }

        MapRenderer renderer = client.getMapRenderer();
        MapRenderState renderState = new MapRenderState();
        renderer.extractRenderState(mapId, mapData, renderState);
        MaptipPreviewRenderer.extractMap(graphics, x, y, sizePx, renderState);
    }
}
