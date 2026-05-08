package com.imeetake.maptip.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class MaptipTooltipComponent implements ClientTooltipComponent {
    private static final int FRAME_PIXELS = 128;
    private static final int FRAME_INSET_SRC = 7;
    private static final int MAP_PIXELS = 128;
    private static final int SIZE_PX = 96;
    private static final int BOTTOM_PADDING = 6;
    private static final Identifier MAP_FRAME = Identifier.withDefaultNamespace("textures/map/map_background.png");

    private final ItemStack stack;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(Font font) {
        return SIZE_PX;
    }

    @Override
    public int getHeight(Font font) {
        return SIZE_PX + BOTTOM_PADDING;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        float frameScale = SIZE_PX / (float) FRAME_PIXELS;
        float offsetPxFloat = FRAME_INSET_SRC * frameScale;
        int offsetPx = Math.round(offsetPxFloat);
        int innerPx = Math.round(SIZE_PX - offsetPxFloat * 2f);
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(frameScale, frameScale);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MAP_FRAME, 0, 0, 0, 0, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS);
        guiGraphics.pose().popMatrix();

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

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + offsetPx, y + offsetPx);
        guiGraphics.pose().scale(innerScale, innerScale);
        guiGraphics.submitMapRenderState(renderState);
        guiGraphics.pose().popMatrix();
    }
}
