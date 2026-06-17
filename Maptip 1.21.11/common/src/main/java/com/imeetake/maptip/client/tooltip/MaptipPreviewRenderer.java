package com.imeetake.maptip.client.tooltip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.resources.Identifier;

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
    private static final Identifier MAP_FRAME = Identifier.withDefaultNamespace("textures/map/map_background.png");

    private MaptipPreviewRenderer() {
    }

    public static void renderFrame(GuiGraphics guiGraphics, int x, int y, int sizePx) {
        float frameScale = sizePx / (float) FRAME_PIXELS;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(frameScale, frameScale);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MAP_FRAME, 0, 0, 0, 0, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS);
        guiGraphics.pose().popMatrix();
    }

    public static void renderMap(GuiGraphics guiGraphics, int x, int y, int sizePx, MapRenderState renderState) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + offsetPx, y + offsetPx);
        guiGraphics.pose().scale(innerScale, innerScale);
        guiGraphics.submitMapRenderState(renderState);
        guiGraphics.pose().popMatrix();
    }

    public static void renderSampleMap(GuiGraphics guiGraphics, int x, int y, int sizePx) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        int innerX = x + offsetPx;
        int innerY = y + offsetPx;
        float innerScale = innerPx / (float) MAP_PIXELS;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(innerX, innerY);
        guiGraphics.pose().scale(innerScale, innerScale);
        for (int tileY = 0; tileY < SAMPLE_GRID_TILES; tileY++) {
            for (int tileX = 0; tileX < SAMPLE_GRID_TILES; tileX++) {
                int minX = tileX * SAMPLE_TILE_PIXELS;
                int minY = tileY * SAMPLE_TILE_PIXELS;
                guiGraphics.fill(minX, minY, minX + SAMPLE_TILE_PIXELS, minY + SAMPLE_TILE_PIXELS,
                    SAMPLE_GRID_COLORS[tileY][tileX]);
            }
        }

        guiGraphics.renderOutline(0, 0, MAP_PIXELS, MAP_PIXELS, SAMPLE_BORDER_COLOR);
        guiGraphics.pose().popMatrix();
    }

    private static int getFrameInset(int sizePx) {
        return Math.round(FRAME_INSET_SRC * (sizePx / (float) FRAME_PIXELS));
    }

    private static int getInnerSize(int sizePx) {
        return sizePx - 2 * getFrameInset(sizePx);
    }
}
