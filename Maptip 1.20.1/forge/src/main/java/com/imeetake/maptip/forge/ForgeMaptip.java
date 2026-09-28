package com.imeetake.maptip.forge;

import com.imeetake.maptip.Maptip;
import com.imeetake.maptip.forge.client.ForgeMaptipClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Maptip.MOD_ID)
public class ForgeMaptip {
    public ForgeMaptip(FMLJavaModLoadingContext context) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ForgeMaptipClient.initialize(context));
    }
}
