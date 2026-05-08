package com.imeetake.maptip.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class MaptipTooltipComponent implements ClientTooltipComponent {
    private static final int SIZE_PX = 96;
    private static final int MAP_PIXELS = 128;
    private static final int INNER_OFFSET_SRC = 7;
    private static final int BOTTOM_PADDING = 6;
    private static final ResourceLocation MAP_FRAME = new ResourceLocation("minecraft", "textures/map/map_background.png");

    private final ItemStack stack;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(Font font) {
        return SIZE_PX;
    }

    @Override
    public int getHeight() {
        return SIZE_PX + BOTTOM_PADDING;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        int offsetPx = Math.round(SIZE_PX * (INNER_OFFSET_SRC / (float) MAP_PIXELS));
        int innerPx = SIZE_PX - offsetPx * 2;

        float frameScale = SIZE_PX / (float) MAP_PIXELS;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 500);
        guiGraphics.pose().scale(frameScale, frameScale, 1f);
        guiGraphics.blit(MAP_FRAME, 0, 0, 0, 0, MAP_PIXELS, MAP_PIXELS, MAP_PIXELS, MAP_PIXELS);
        guiGraphics.pose().popPose();

        Integer mapId = MapItem.getMapId(stack);
        if (mapId == null) {
            return;
        }

        MapItemSavedData mapData = MapItem.getSavedData(mapId, client.level);
        if (mapData == null) {
            int itemX = x + offsetPx + (innerPx - 16) / 2;
            int itemY = y + offsetPx + (innerPx - 16) / 2;
            guiGraphics.renderItem(stack, itemX, itemY);
            return;
        }

        MapRenderer renderer = client.gameRenderer.getMapRenderer();
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + offsetPx, y + offsetPx, 501);
        guiGraphics.pose().scale(innerScale, innerScale, 1f);
        renderer.render(guiGraphics.pose(), guiGraphics.bufferSource(), mapId, mapData, true, LightTexture.FULL_BRIGHT);
        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }
}
