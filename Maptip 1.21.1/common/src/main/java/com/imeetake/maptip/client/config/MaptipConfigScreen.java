package com.imeetake.maptip.client.config;

import com.imeetake.maptip.client.tooltip.MaptipPreviewRenderer;
import com.imeetake.maptip.config.MaptipConfig;
import java.util.function.IntConsumer;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MaptipConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("maptip.configuration.client.title");
    private static final Component PREVIEW_TITLE = Component.translatable("maptip.configuration.preview.title");
    private static final Component RESET = Component.translatable("maptip.configuration.reset");
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int ID_TEXT_COLOR = 0xFFA0A0A0;
    private static final int PANEL_COLOR = 0x78101010;
    private static final int PANEL_BORDER_COLOR = 0xB4FFFFFF;
    private static final int TOOLTIP_BACKGROUND = 0xF0100010;
    private static final int TOOLTIP_BORDER = 0xA0FFFFFF;
    private static final int PANEL_PADDING = 10;
    private static final int COLUMN_GAP = 18;
    private static final int MAX_CONTENT_WIDTH = 620;
    private static final int CONTROL_WIDTH = 220;

    private final Screen parent;
    private int previewSize;
    private boolean requireShift;
    private PreviewSizeSlider previewSizeSlider;
    private int contentLeft;
    private int contentTop;
    private int contentWidth;
    private int controlsLeft;
    private int controlsWidth;
    private int previewLeft;
    private int previewWidth;
    private int panelHeight;

    public MaptipConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.previewSize = MaptipConfig.getPreviewSize();
        this.requireShift = MaptipConfig.getRequireShift();
    }

    @Override
    protected void init() {
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

        this.addRenderableOnly((guiGraphics, mouseX, mouseY, tickDelta) -> this.renderScreenContent(guiGraphics));

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
            Balm.getConfig().updateLocalConfig(MaptipConfig.class, config -> {
                config.previewSize = savedPreviewSize;
                config.requireShift = savedRequireShift;
            });
            this.minecraft.setScreen(this.parent);
        }).bounds(doneButtonX, buttonY + 28, doneButtonWidth, 20).build());
    }

    private void renderScreenContent(GuiGraphics guiGraphics) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 16, TEXT_COLOR);
        this.renderControlsPanel(guiGraphics);
        this.renderPreview(guiGraphics);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void renderControlsPanel(GuiGraphics guiGraphics) {
        guiGraphics.fill(this.controlsLeft, this.contentTop, this.controlsLeft + this.controlsWidth, this.contentTop + this.panelHeight, PANEL_COLOR);
        guiGraphics.renderOutline(this.controlsLeft, this.contentTop, this.controlsWidth, this.panelHeight, PANEL_BORDER_COLOR);
        guiGraphics.drawString(this.font, Component.translatable("maptip.configuration.previewSize"), this.controlsLeft + PANEL_PADDING, this.contentTop + 16, TEXT_COLOR);
    }

    private void renderPreview(GuiGraphics guiGraphics) {
        Component mapName = Component.translatable("item.minecraft.filled_map");
        Component mapId = Component.translatable("filled_map.id", 0);
        int sampleTextWidth = Math.max(this.font.width(mapName), this.font.width(mapId));
        int tooltipContentWidth = Math.max(this.previewSize, sampleTextWidth);
        int tooltipContentHeight = 26 + this.previewSize;
        float scale = Math.min(1f, Math.min(
            (this.previewWidth - PANEL_PADDING * 2) / (float) (tooltipContentWidth + 24),
            (this.panelHeight - PANEL_PADDING * 2 - 18) / (float) (tooltipContentHeight + 24)
        ));

        guiGraphics.fill(this.previewLeft, this.contentTop, this.previewLeft + this.previewWidth, this.contentTop + this.panelHeight, PANEL_COLOR);
        guiGraphics.renderOutline(this.previewLeft, this.contentTop, this.previewWidth, this.panelHeight, PANEL_BORDER_COLOR);
        guiGraphics.drawString(this.font, PREVIEW_TITLE, this.previewLeft + PANEL_PADDING, this.contentTop + 16, TEXT_COLOR);
        if (scale < 0.35f) {
            return;
        }

        int tooltipX = this.previewLeft + this.previewWidth / 2;
        int tooltipY = this.contentTop + this.panelHeight / 2 + 6;
        int tooltipWidth = tooltipContentWidth + 8;
        int tooltipHeight = tooltipContentHeight + 8;
        int mapY = -tooltipHeight / 2 + 17;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(tooltipX, tooltipY, 0);
        guiGraphics.pose().scale(scale, scale, 1f);
        guiGraphics.fill(-tooltipWidth / 2 - 3, -tooltipHeight / 2 - 4, tooltipWidth / 2 + 3, tooltipHeight / 2 + 4, TOOLTIP_BACKGROUND);
        guiGraphics.renderOutline(-tooltipWidth / 2 - 3, -tooltipHeight / 2 - 4, tooltipWidth + 6, tooltipHeight + 8, TOOLTIP_BORDER);
        guiGraphics.drawString(this.font, mapName, -tooltipContentWidth / 2, -tooltipHeight / 2 + 4, TEXT_COLOR);
        MaptipPreviewRenderer.renderFrame(guiGraphics, -tooltipContentWidth / 2, mapY, this.previewSize, 1);
        MaptipPreviewRenderer.renderSampleMap(guiGraphics, -tooltipContentWidth / 2, mapY, this.previewSize, 2);
        guiGraphics.drawString(this.font, mapId, -tooltipContentWidth / 2, mapY + this.previewSize + 6, ID_TEXT_COLOR, true);
        guiGraphics.pose().popPose();
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
