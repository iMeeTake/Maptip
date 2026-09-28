package com.imeetake.maptip;

import com.imeetake.maptip.config.MaptipConfig;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;

public class Maptip {
    public static final String MOD_ID = "maptip";

    public static void initialize(BalmRegistrars registrars) {
        Balm.config().registerConfig(MaptipConfig.class);
    }
}
