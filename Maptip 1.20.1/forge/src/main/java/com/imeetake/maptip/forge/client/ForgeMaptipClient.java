package com.imeetake.maptip.forge.client;

import com.mojang.datafixers.util.Either;
import com.imeetake.maptip.Maptip;
import com.imeetake.maptip.client.MaptipClient;
import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;

import java.util.List;

public class ForgeMaptipClient {
    public static void initialize(FMLJavaModLoadingContext context) {
        BalmClient.initializeMod(Maptip.MOD_ID, EmptyLoadContext.INSTANCE, MaptipClient::initialize);
        context.getModEventBus().addListener(ForgeMaptipClient::registerTooltipComponents);
        MinecraftForge.EVENT_BUS.addListener(ForgeMaptipClient::gatherTooltipComponents);
    }

    private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(MaptipTooltipData.class, MaptipClient::createTooltipComponent);
    }

    private static void gatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.FILLED_MAP) || MapItem.getMapId(stack) == null) {
            return;
        }

        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        boolean alreadyAdded = elements.stream().anyMatch(element -> element.map(text -> false, component -> component instanceof MaptipTooltipData));
        if (!alreadyAdded) {
            elements.add(Math.min(1, elements.size()), Either.right(new MaptipTooltipData(stack.copy())));
        }
    }
}
