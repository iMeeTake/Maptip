package com.imeetake.maptip.client.tooltip;

import com.imeetake.maptip.config.MaptipConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;

public final class MaptipTooltipComponent implements ClientTooltipComponent {
    private static final int BOTTOM_PADDING = 6;
    private static final int HINT_HEIGHT = 10;
    private static final int HINT_COLOR = 0xFFAAAAAA;
    private static final Component NOT_LOADED = Component.translatable("maptip.tooltip.notLoaded");
    private static final Component NOT_EXPLORED = Component.translatable("maptip.tooltip.notExplored");

    private final ItemStack stack;
    private final MapItemSavedData mapData;
    private final Component hint;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
        this.mapData = findMapData(stack);
        this.hint = findHint(this.mapData);
    }

    @Override
    public int getWidth(Font font) {
        if (hint != null) {
            return font.width(hint);
        }

        return MaptipConfig.getPreviewSize();
    }

    @Override
    public int getHeight() {
        if (hint != null) {
            return HINT_HEIGHT;
        }

        return MaptipConfig.getPreviewSize() + BOTTOM_PADDING;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        if (hint != null) {
            font.drawInBatch(hint.getVisualOrderText(), x, y, HINT_COLOR, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        Integer mapId = MapItem.getMapId(stack);
        if (hint != null || mapId == null || mapData == null) {
            return;
        }

        int sizePx = MaptipConfig.getPreviewSize();
        MaptipPreviewRenderer.renderFrame(guiGraphics, x, y, sizePx);

        MapRenderer renderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
        MaptipPreviewRenderer.renderMap(guiGraphics, x, y, sizePx, renderer, mapId, mapData);
    }

    private static MapItemSavedData findMapData(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();
        Integer mapId = MapItem.getMapId(stack);
        if (client.level == null || mapId == null) {
            return null;
        }

        return MapItem.getSavedData(mapId, client.level);
    }

    private static Component findHint(MapItemSavedData mapData) {
        if (mapData == null) {
            return NOT_LOADED;
        }

        if (!mapData.locked && isBlank(mapData)) {
            return NOT_EXPLORED;
        }

        return null;
    }

    private static boolean isBlank(MapItemSavedData mapData) {
        for (byte color : mapData.colors) {
            if (color != 0) {
                return false;
            }
        }

        return true;
    }
}
