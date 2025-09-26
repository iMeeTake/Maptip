package com.imeetake.maptip.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public final class MaptipTooltipComponent implements TooltipComponent {
    private static final int SIZE_PX = 96;
    private static final int MAP_PIXELS = 128;
    private static final int INNER_OFFSET_SRC = 7;
    private static final int BOTTOM_PADDING = 6;
    private static final Identifier MAP_FRAME = Identifier.of("minecraft", "textures/map/map_background.png");

    private final ItemStack stack;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return SIZE_PX;
    }

    @Override
    public int getHeight() {
        return SIZE_PX + BOTTOM_PADDING;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;

        int offsetPx = Math.round(SIZE_PX * (INNER_OFFSET_SRC / (float) MAP_PIXELS));
        int innerPx = SIZE_PX - offsetPx * 2;

        float frameScale = SIZE_PX / (float) MAP_PIXELS;
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 500);
        context.getMatrices().scale(frameScale, frameScale, 1f);
        context.drawTexture(MAP_FRAME, 0, 0, 0, 0, MAP_PIXELS, MAP_PIXELS, MAP_PIXELS, MAP_PIXELS);
        context.getMatrices().pop();

        MapIdComponent id = this.stack.get(DataComponentTypes.MAP_ID);
        if (id == null) return;

        MapState state = FilledMapItem.getMapState(id, client.world);
        if (state == null) {
            int ix = x + offsetPx + (innerPx - 16) / 2;
            int iy = y + offsetPx + (innerPx - 16) / 2;
            context.drawItem(this.stack, ix, iy);
            return;
        }

        MapRenderer renderer = client.gameRenderer.getMapRenderer();
        float innerScale = innerPx / (float) MAP_PIXELS;

        context.getMatrices().push();
        context.getMatrices().translate(x + offsetPx, y + offsetPx, 501);
        context.getMatrices().scale(innerScale, innerScale, 1f);
        VertexConsumerProvider.Immediate vcp = context.getVertexConsumers();
        renderer.draw(context.getMatrices(), vcp, id, state, true, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        vcp.draw();
        context.getMatrices().pop();
    }
}
