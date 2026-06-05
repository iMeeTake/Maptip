package com.imeetake.maptip.fabric.client;

import com.imeetake.maptip.client.config.MaptipConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class FabricMaptipModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MaptipConfigScreen::new;
    }
}
