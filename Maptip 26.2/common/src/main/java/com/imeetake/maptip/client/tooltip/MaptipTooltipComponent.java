package com.imeetake.maptip.client.tooltip;

import com.imeetake.maptip.config.MaptipConfig;
import java.util.Map;
import java.util.WeakHashMap;
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
    private static final Map<MapItemSavedData, BlankState> BLANK_CACHE = new WeakHashMap<>();

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

    public static boolean isBlank(MapItemSavedData mapData) {
        Minecraft client = Minecraft.getInstance();
        long gameTime = client.level != null ? client.level.getGameTime() : Long.MIN_VALUE;
        BlankState state = BLANK_CACHE.get(mapData);
        if (state != null) {
            if (state.nonZeroIndex >= 0 && state.nonZeroIndex < mapData.colors.length && mapData.colors[state.nonZeroIndex] != 0) {
                return false;
            }
            if (state.nonZeroIndex == -1 && gameTime != Long.MIN_VALUE && gameTime == state.blankGameTime) {
                return true;
            }
        } else {
            state = new BlankState();
            BLANK_CACHE.put(mapData, state);
        }

        byte[] colors = mapData.colors;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] != 0) {
                state.nonZeroIndex = i;
                state.blankGameTime = Long.MIN_VALUE;
                return false;
            }
        }

        state.nonZeroIndex = -1;
        state.blankGameTime = gameTime;
        return true;
    }

    private static final class BlankState {
        private int nonZeroIndex = -1;
        private long blankGameTime = Long.MIN_VALUE;
    }
}
