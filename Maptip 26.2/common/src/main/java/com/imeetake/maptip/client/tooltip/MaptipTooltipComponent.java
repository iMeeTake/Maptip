package com.imeetake.maptip.client.tooltip;

import com.imeetake.maptip.config.MaptipConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jspecify.annotations.Nullable;

public final class MaptipTooltipComponent implements ClientTooltipComponent {
    private static final int BOTTOM_PADDING = 6;
    private static final int HINT_HEIGHT = 10;
    private static final int HINT_COLOR = 0xFFAAAAAA;
    private static final Component NOT_LOADED = Component.translatable("maptip.tooltip.notLoaded");
    private static final Component NOT_EXPLORED = Component.translatable("maptip.tooltip.notExplored");
    private static final MapRenderState RENDER_STATE = new MapRenderState();

    private final ItemStack stack;
    private final @Nullable MapItemSavedData mapData;
    private final @Nullable Component hint;

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
    public int getHeight(Font font) {
        if (hint != null) {
            return HINT_HEIGHT;
        }

        return MaptipConfig.getPreviewSize() + BOTTOM_PADDING;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        if (hint != null) {
            graphics.text(font, hint, x, y, HINT_COLOR, true);
        }
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (hint != null || mapId == null || mapData == null) {
            return;
        }

        int sizePx = MaptipConfig.getPreviewSize();
        MaptipPreviewRenderer.extractFrame(graphics, x, y, sizePx);

        MapRenderer renderer = Minecraft.getInstance().getMapRenderer();
        renderer.extractRenderState(mapId, mapData, RENDER_STATE);
        MaptipPreviewRenderer.extractMap(graphics, x, y, sizePx, RENDER_STATE);
    }

    private static @Nullable MapItemSavedData findMapData(ItemStack stack) {
        Minecraft client = Minecraft.getInstance();
        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (client.level == null || mapId == null) {
            return null;
        }

        return MapItem.getSavedData(mapId, client.level);
    }

    private static @Nullable Component findHint(@Nullable MapItemSavedData mapData) {
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
