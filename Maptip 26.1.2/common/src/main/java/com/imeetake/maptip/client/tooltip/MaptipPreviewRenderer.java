package com.imeetake.maptip.client.tooltip;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public final class MaptipPreviewRenderer {
    private static final int FRAME_PIXELS = 128;
    private static final int FRAME_INSET_SRC = 7;
    private static final int MAP_PIXELS = 128;
    private static final int SAMPLE_GRID_TILES = 4;
    private static final int SAMPLE_TILE_PIXELS = MAP_PIXELS / SAMPLE_GRID_TILES;
    private static final int[][] SAMPLE_GRID_COLORS = {
        {
            ARGB.color(255, 65, 102, 148),
            ARGB.color(255, 76, 114, 88),
            ARGB.color(255, 96, 132, 80),
            ARGB.color(255, 55, 103, 70)
        },
        {
            ARGB.color(255, 190, 171, 105),
            ARGB.color(255, 76, 114, 88),
            ARGB.color(255, 96, 132, 80),
            ARGB.color(255, 65, 102, 148)
        },
        {
            ARGB.color(255, 76, 114, 88),
            ARGB.color(255, 190, 171, 105),
            ARGB.color(255, 154, 122, 78),
            ARGB.color(255, 96, 132, 80)
        },
        {
            ARGB.color(255, 119, 111, 100),
            ARGB.color(255, 76, 114, 88),
            ARGB.color(255, 65, 102, 148),
            ARGB.color(255, 190, 171, 105)
        }
    };
    private static final int SAMPLE_BORDER_COLOR = ARGB.color(160, 43, 35, 24);
    private static final Identifier MAP_FRAME = Identifier.withDefaultNamespace("textures/map/map_background.png");

    private MaptipPreviewRenderer() {
    }

    public static void extractFrame(GuiGraphicsExtractor graphics, int x, int y, int sizePx) {
        float frameScale = sizePx / (float) FRAME_PIXELS;

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(frameScale, frameScale);
        graphics.blit(RenderPipelines.GUI_TEXTURED, MAP_FRAME, 0, 0, 0, 0, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS, FRAME_PIXELS);
        graphics.pose().popMatrix();
    }

    public static void extractMap(GuiGraphicsExtractor graphics, int x, int y, int sizePx, MapRenderState renderState) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        float innerScale = innerPx / (float) MAP_PIXELS;

        graphics.pose().pushMatrix();
        graphics.pose().translate(x + offsetPx, y + offsetPx);
        graphics.pose().scale(innerScale, innerScale);
        graphics.map(renderState);
        graphics.pose().popMatrix();
    }

    public static void extractSampleMap(GuiGraphicsExtractor graphics, int x, int y, int sizePx) {
        int offsetPx = getFrameInset(sizePx);
        int innerPx = getInnerSize(sizePx);
        int innerX = x + offsetPx;
        int innerY = y + offsetPx;
        float innerScale = innerPx / (float) MAP_PIXELS;

        graphics.pose().pushMatrix();
        graphics.pose().translate(innerX, innerY);
        graphics.pose().scale(innerScale, innerScale);
        for (int tileY = 0; tileY < SAMPLE_GRID_TILES; tileY++) {
            for (int tileX = 0; tileX < SAMPLE_GRID_TILES; tileX++) {
                int minX = tileX * SAMPLE_TILE_PIXELS;
                int minY = tileY * SAMPLE_TILE_PIXELS;
                graphics.fill(minX, minY, minX + SAMPLE_TILE_PIXELS, minY + SAMPLE_TILE_PIXELS,
                    SAMPLE_GRID_COLORS[tileY][tileX]);
            }
        }

        graphics.outline(0, 0, MAP_PIXELS, MAP_PIXELS, SAMPLE_BORDER_COLOR);
        graphics.pose().popMatrix();
    }

    private static int getFrameInset(int sizePx) {
        return Math.round(FRAME_INSET_SRC * (sizePx / (float) FRAME_PIXELS));
    }

    private static int getInnerSize(int sizePx) {
        return Math.round(sizePx - FRAME_INSET_SRC * (sizePx / (float) FRAME_PIXELS) * 2f);
    }
}
