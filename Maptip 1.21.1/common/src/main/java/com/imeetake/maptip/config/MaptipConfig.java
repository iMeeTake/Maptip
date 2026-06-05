package com.imeetake.maptip.config;

import com.imeetake.maptip.Maptip;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.minecraft.util.Mth;

@Config(value = Maptip.MOD_ID, type = "client")
public class MaptipConfig {
    public static final int DEFAULT_PREVIEW_SIZE = 80;
    public static final int MIN_PREVIEW_SIZE = 48;
    public static final int MAX_PREVIEW_SIZE = 144;

    @Comment("Size of the map preview in item tooltips, in pixels.")
    public int previewSize = DEFAULT_PREVIEW_SIZE;

    public static int getPreviewSize() {
        MaptipConfig config = Balm.getConfig().getActiveConfig(MaptipConfig.class);
        if (config == null) {
            return DEFAULT_PREVIEW_SIZE;
        }

        return Mth.clamp(config.previewSize, MIN_PREVIEW_SIZE, MAX_PREVIEW_SIZE);
    }
}
