package com.imeetake.maptip.client.tooltip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class MaptipPreviewRenderer {
    private static final int FRAME_PIXELS = 128;
    private static final int FRAME_INSET_SRC = 7;
    private static final int MAP_PIXELS = 128;
    private static final int SAMPLE_GRID_TILES = 4;
    private static final int SAMPLE_TILE_PIXELS = MAP_PIXELS / SAMPLE_GRID_TILES;
    private static final int[][] SAMPLE_GRID_COLORS = {
        {
            0xFF416694,
            0xFF4C7258,
            0xFF608450,
            0xFF376746
        },
        {
            0xFFBEAB69,
            0xFF4C7258,
            0xFF608450,
            0xFF416694
        },
        {
            0xFF4C7258,
            0xFFBEAB69,
            0xFF9A7A4E,
            0xFF608450
        },
        {
            0xFF776F64,
            0xFF4C7258,
            0xFF416694,
            0xFFBEAB69
        }
    };
    private static final int SAMPLE_BORDER_COLOR = 0xA02B2318;
    private static final ResourceLocation MAP_FRAME = ResourceLocation.withDefaultNamespace("textures/map/map_background.png");

    private MaptipPreviewRenderer() {
    }

    public static void renderFrame(GuiGraphics guiGraphics, int x, int y, int sizePx) {
        renderFrame(guiGraphics, x, y, sizePx, 500);
    }

    public static void renderFrame(GuiGraphics guiGraphics, int x, int y, int sizePx, int z) {
        float frameScale = sizePx / (float) FRAME_PIXELS;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, z);
        guiGraphics.pose().scale(frameScale, frameScale, 1f);
        guiGraphics.blit(MAP_FRAME, 0, 0, 0, 0, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS);
        guiGraphics.pose().popPose();
    }

    public static void renderMap(GuiGraphics guiGraphics, int x, int y, int sizePx, MapRenderer renderer, MapId mapId, MapItemSavedData mapData) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + offsetPx, y + offsetPx, 501);
        guiGraphics.pose().scale(innerScale, innerScale, 1f);
        renderer.render(guiGraphics.pose(), guiGraphics.bufferSource(), mapId, mapData, true, LightTexture.FULL_BRIGHT);
        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }

    public static void renderSampleMap(GuiGraphics guiGraphics, int x, int y, int sizePx, int z) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        int innerX = x + offsetPx;
        int innerY = y + offsetPx;
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(innerX, innerY, z);
        guiGraphics.pose().scale(innerScale, innerScale, 1f);
        for (int tileY = 0; tileY < SAMPLE_GRID_TILES; tileY++) {
            for (int tileX = 0; tileX < SAMPLE_GRID_TILES; tileX++) {
                int minX = tileX * SAMPLE_TILE_PIXELS;
                int minY = tileY * SAMPLE_TILE_PIXELS;
                guiGraphics.fill(minX, minY, minX + SAMPLE_TILE_PIXELS, minY + SAMPLE_TILE_PIXELS,
                    SAMPLE_GRID_COLORS[tileY][tileX]);
            }
        }

        guiGraphics.renderOutline(0, 0, MAP_PIXELS, MAP_PIXELS, SAMPLE_BORDER_COLOR);
        guiGraphics.pose().popPose();
    }

    private static int getFrameInset(int sizePx) {
        return Math.round(FRAME_INSET_SRC * (sizePx / (float) FRAME_PIXELS));
    }

    private static int getInnerSize(int sizePx) {
        return sizePx - 2 * getFrameInset(sizePx);
    }
}
