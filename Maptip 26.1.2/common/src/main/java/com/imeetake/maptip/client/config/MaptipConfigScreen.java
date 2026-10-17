package com.imeetake.maptip.client.config;

import com.imeetake.maptip.client.tooltip.MaptipPreviewRenderer;
import com.imeetake.maptip.client.tooltip.MaptipTooltipComponent;
import com.imeetake.maptip.config.MaptipConfig;
import java.util.List;
import java.util.function.IntConsumer;
import net.blay09.mods.balm.Balm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public class MaptipConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("maptip.configuration.client.title");
    private static final Component PREVIEW_TITLE = Component.translatable("maptip.configuration.preview.title");
    private static final Component RESET = Component.translatable("maptip.configuration.reset");
    private static final Component DEFAULT_MAP_NAME = Component.translatable("item.minecraft.filled_map");
    private static final Component DEFAULT_MAP_ID = Component.translatable("filled_map.id", 0);
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int ID_TEXT_COLOR = 0xFFA0A0A0;
    private static final int PANEL_COLOR = ARGB.color(120, 16, 16, 16);
    private static final int PANEL_BORDER_COLOR = ARGB.color(180, 255, 255, 255);
    private static final int PANEL_PADDING = 10;
    private static final int COLUMN_GAP = 18;
    private static final int MAX_CONTENT_WIDTH = 620;
    private static final int CONTROL_WIDTH = 220;

    private final @Nullable Screen parent;
    private int previewSize;
    private boolean requireShift;
    private @Nullable PreviewMap previewMap;
    private @Nullable PreviewSizeSlider previewSizeSlider;
    private int contentLeft;
    private int contentTop;
    private int contentWidth;
    private int controlsLeft;
    private int controlsWidth;
    private int previewLeft;
    private int previewWidth;
    private int panelHeight;

    public MaptipConfigScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.previewSize = MaptipConfig.getPreviewSize();
        this.requireShift = MaptipConfig.getRequireShift();
    }

    @Override
    protected void init() {
        this.previewMap = this.findPreviewMap();
        this.contentWidth = Math.min(MAX_CONTENT_WIDTH, Math.max(1, this.width - 40));
        this.contentLeft = this.width / 2 - this.contentWidth / 2;
        this.contentTop = Math.max(42, Math.min(64, this.height / 8 + 20));
        this.controlsWidth = Math.min(CONTROL_WIDTH, Math.max(130, this.contentWidth / 2 - COLUMN_GAP / 2));
        this.controlsLeft = this.contentLeft;
        this.previewLeft = this.controlsLeft + this.controlsWidth + COLUMN_GAP;
        this.previewWidth = Math.max(0, this.contentLeft + this.contentWidth - this.previewLeft);
        this.panelHeight = Math.max(162, this.height - this.contentTop - 48);
        this.contentTop = Math.min(this.contentTop, Math.max(20, this.height - this.panelHeight - 10));

        int buttonY = this.contentTop + this.panelHeight - 52;
        int buttonGap = 8;
        int buttonWidth = this.controlsWidth - PANEL_PADDING * 2;
        int halfButtonWidth = (buttonWidth - buttonGap) / 2;
        int doneButtonX = this.controlsLeft + PANEL_PADDING + halfButtonWidth + buttonGap;
        int doneButtonWidth = buttonWidth - halfButtonWidth - buttonGap;

        this.previewSizeSlider = this.addRenderableWidget(new PreviewSizeSlider(
            this.controlsLeft + PANEL_PADDING,
            this.contentTop + 48,
            this.controlsWidth - PANEL_PADDING * 2,
            20,
            this.previewSize,
            value -> this.previewSize = value
        ));
        this.previewSizeSlider.setTooltip(Tooltip.create(Component.translatable("maptip.configuration.previewSize.tooltip")));

        this.addRenderableWidget(Checkbox.builder(Component.translatable("maptip.configuration.requireShift"), this.font)
            .pos(this.controlsLeft + PANEL_PADDING, this.contentTop + 72)
            .maxWidth(this.controlsWidth - PANEL_PADDING * 2)
            .selected(this.requireShift)
            .onValueChange((checkbox, value) -> this.requireShift = value)
            .tooltip(Tooltip.create(Component.translatable("maptip.configuration.requireShift.tooltip")))
            .build());

        this.addRenderableWidget(Button.builder(RESET, button -> {
            this.previewSize = MaptipConfig.DEFAULT_PREVIEW_SIZE;
            this.requireShift = MaptipConfig.DEFAULT_REQUIRE_SHIFT;
            this.rebuildWidgets();
        }).bounds(this.controlsLeft + PANEL_PADDING, buttonY, this.controlsWidth - PANEL_PADDING * 2, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.parent))
            .bounds(this.controlsLeft + PANEL_PADDING, buttonY + 28, halfButtonWidth, 20)
            .build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            int savedPreviewSize = this.previewSize;
            boolean savedRequireShift = this.requireShift;
            Balm.config().updateLocalConfig(MaptipConfig.class, config -> {
                config.previewSize = savedPreviewSize;
                config.requireShift = savedRequireShift;
            });
            this.minecraft.setScreen(this.parent);
        }).bounds(doneButtonX, buttonY + 28, doneButtonWidth, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.centeredText(this.font, this.title, this.width / 2, 16, TEXT_COLOR);
        this.extractControlsPanel(graphics);
        this.extractPreview(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void extractControlsPanel(GuiGraphicsExtractor graphics) {
        graphics.fill(this.controlsLeft, this.contentTop, this.controlsLeft + this.controlsWidth, this.contentTop + this.panelHeight, PANEL_COLOR);
        graphics.outline(this.controlsLeft, this.contentTop, this.controlsWidth, this.panelHeight, PANEL_BORDER_COLOR);
        graphics.text(this.font, Component.translatable("maptip.configuration.previewSize"), this.controlsLeft + PANEL_PADDING, this.contentTop + 16, TEXT_COLOR);
    }

    private void extractPreview(GuiGraphicsExtractor graphics) {
        PreviewMap previewMap = this.previewMap;
        Component mapName = previewMap != null ? previewMap.name() : DEFAULT_MAP_NAME;
        Component mapId = previewMap != null ? previewMap.idText() : DEFAULT_MAP_ID;
        int sampleTextWidth = Math.max(this.font.width(mapName), this.font.width(mapId));
        int tooltipContentWidth = Math.max(this.previewSize, sampleTextWidth);
        int tooltipContentHeight = 26 + this.previewSize;
        float scale = Math.min(1f, Math.min(
            (this.previewWidth - PANEL_PADDING * 2) / (float) (tooltipContentWidth + 24),
            (this.panelHeight - PANEL_PADDING * 2 - 18) / (float) (tooltipContentHeight + 24)
        ));

        graphics.fill(this.previewLeft, this.contentTop, this.previewLeft + this.previewWidth, this.contentTop + this.panelHeight, PANEL_COLOR);
        graphics.outline(this.previewLeft, this.contentTop, this.previewWidth, this.panelHeight, PANEL_BORDER_COLOR);
        graphics.text(this.font, PREVIEW_TITLE, this.previewLeft + PANEL_PADDING, this.contentTop + 16, TEXT_COLOR);
        if (scale < 0.35f) {
            return;
        }

        int tooltipX = this.previewLeft + this.previewWidth / 2;
        int tooltipY = this.contentTop + this.panelHeight / 2 + 6;
        List<ClientTooltipComponent> tooltipLines = List.of(
            ClientTooltipComponent.create(mapName.getVisualOrderText()),
            new SampleMapTooltipComponent(this.previewSize, previewMap),
            new GrayTextTooltipComponent(mapId)
        );

        graphics.pose().pushMatrix();
        graphics.pose().translate(tooltipX, tooltipY);
        graphics.pose().scale(scale, scale);
        graphics.tooltip(
            this.font,
            tooltipLines,
            0,
            0,
            centeredTooltipPositioner(),
            null
        );
        graphics.pose().popMatrix();
    }

    private @Nullable PreviewMap findPreviewMap() {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.level == null) {
            return null;
        }

        ClientLevel level = this.minecraft.level;
        PreviewMap mainHand = resolvePreviewMap(this.minecraft.player.getMainHandItem(), level);
        if (mainHand != null) {
            return mainHand;
        }

        PreviewMap offHand = resolvePreviewMap(this.minecraft.player.getOffhandItem(), level);
        if (offHand != null) {
            return offHand;
        }

        Inventory inventory = this.minecraft.player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            PreviewMap candidate = resolvePreviewMap(inventory.getItem(i), level);
            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private static @Nullable PreviewMap resolvePreviewMap(ItemStack stack, ClientLevel level) {
        if (!(stack.getItem() instanceof MapItem)) {
            return null;
        }

        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (mapId == null) {
            return null;
        }

        MapItemSavedData mapData = MapItem.getSavedData(mapId, level);
        if (mapData == null || (!mapData.locked && MaptipTooltipComponent.isBlank(mapData))) {
            return null;
        }

        return new PreviewMap(stack.getHoverName(), Component.translatable("filled_map.id", mapId.id()), mapId, mapData);
    }

    private static ClientTooltipPositioner centeredTooltipPositioner() {
        return (screenWidth, screenHeight, x, y, tooltipWidth, tooltipHeight) -> new Vector2i(x - tooltipWidth / 2, y - tooltipHeight / 2);
    }

    private record PreviewMap(Component name, Component idText, MapId mapId, MapItemSavedData mapData) {
    }

    private static final class SampleMapTooltipComponent implements ClientTooltipComponent {
        private static final int BOTTOM_PADDING = 6;
        private static final MapRenderState RENDER_STATE = new MapRenderState();

        private final int sizePx;
        private final @Nullable PreviewMap previewMap;

        private SampleMapTooltipComponent(int sizePx, @Nullable PreviewMap previewMap) {
            this.sizePx = sizePx;
            this.previewMap = previewMap;
        }

        @Override
        public int getHeight(Font font) {
            return this.sizePx + BOTTOM_PADDING;
        }

        @Override
        public int getWidth(Font font) {
            return this.sizePx;
        }

        @Override
        public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
            MaptipPreviewRenderer.extractFrame(graphics, x, y, this.sizePx);
            if (this.previewMap != null) {
                MapRenderer renderer = Minecraft.getInstance().getMapRenderer();
                renderer.extractRenderState(this.previewMap.mapId(), this.previewMap.mapData(), RENDER_STATE);
                MaptipPreviewRenderer.extractMap(graphics, x, y, this.sizePx, RENDER_STATE);
            } else {
                MaptipPreviewRenderer.extractSampleMap(graphics, x, y, this.sizePx);
            }
        }
    }

    private static final class GrayTextTooltipComponent implements ClientTooltipComponent {
        private final Component text;

        private GrayTextTooltipComponent(Component text) {
            this.text = text;
        }

        @Override
        public int getHeight(Font font) {
            return 10;
        }

        @Override
        public int getWidth(Font font) {
            return font.width(this.text);
        }

        @Override
        public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
            graphics.text(font, this.text, x, y, ID_TEXT_COLOR, true);
        }
    }

    private static final class PreviewSizeSlider extends AbstractSliderButton {
        private final IntConsumer onChange;
        private int previewSize;

        private PreviewSizeSlider(int x, int y, int width, int height, int previewSize, IntConsumer onChange) {
            super(x, y, width, height, Component.empty(), normalize(previewSize));
            this.previewSize = clamp(previewSize);
            this.onChange = onChange;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("maptip.configuration.previewSize.value", this.previewSize));
        }

        @Override
        protected void applyValue() {
            this.previewSize = denormalize(this.value);
            this.onChange.accept(this.previewSize);
        }

        private static double normalize(int previewSize) {
            return (clamp(previewSize) - MaptipConfig.MIN_PREVIEW_SIZE) / (double) (MaptipConfig.MAX_PREVIEW_SIZE - MaptipConfig.MIN_PREVIEW_SIZE);
        }

        private static int denormalize(double value) {
            int range = MaptipConfig.MAX_PREVIEW_SIZE - MaptipConfig.MIN_PREVIEW_SIZE;
            return clamp(MaptipConfig.MIN_PREVIEW_SIZE + Math.round((float) value * range));
        }

        private static int clamp(int previewSize) {
            return Mth.clamp(previewSize, MaptipConfig.MIN_PREVIEW_SIZE, MaptipConfig.MAX_PREVIEW_SIZE);
        }
    }
}
