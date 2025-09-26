package com.imeetake.maptip.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public final class MaptipTooltipComponent implements TooltipComponent {
    private static final int SRC_BG = 128;
    private static final int FRAME_INSET_SRC = 7;
    private static final int MAP_SRC = 128;
    private static final int SIZE_PX = 96;
    private static final int PADDING_BOTTOM = 6;
    private static final int LIGHT = 0x00F000F0;
    private static final Identifier VANILLA_BG = Identifier.of("minecraft", "textures/map/map_background.png");

    private final ItemStack stack;

    public MaptipTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(TextRenderer tr) {
        return SIZE_PX;
    }

    @Override
    public int getHeight(TextRenderer tr) {
        return SIZE_PX + PADDING_BOTTOM;
    }

    @Override
    public void drawItems(TextRenderer tr, int x, int y, int width, int height, DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        float s = SIZE_PX / (float) SRC_BG;
        float off = FRAME_INSET_SRC * s;
        float innerW = SIZE_PX - 2f * off;
        float innerScale = innerW / (float) MAP_SRC;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 500);
        ctx.getMatrices().scale(s, s, 1f);
        ctx.drawTexture(RenderLayer::getGuiTextured, VANILLA_BG, 0, 0, 0, 0, SRC_BG, SRC_BG, SRC_BG, SRC_BG);
        ctx.getMatrices().pop();

        MapIdComponent id = stack.get(DataComponentTypes.MAP_ID);
        if (id == null) return;
        MapState state = FilledMapItem.getMapState(id, mc.world);
        if (state == null) return;

        MapRenderer renderer = mc.getMapRenderer();
        MapRenderState rs = new MapRenderState();
        renderer.update(id, state, rs);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + off, y + off, 501);
        ctx.getMatrices().scale(innerScale, innerScale, 1f);
        ctx.draw((VertexConsumerProvider vcp) -> renderer.draw(rs, ctx.getMatrices(), vcp, true, LIGHT));
        ctx.getMatrices().pop();
    }
}
