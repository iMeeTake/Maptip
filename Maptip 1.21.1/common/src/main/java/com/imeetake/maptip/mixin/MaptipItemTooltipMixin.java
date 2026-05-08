package com.imeetake.maptip.mixin;

import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class MaptipItemTooltipMixin {
    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void maptip$getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> callbackInfo) {
        if (stack.is(Items.FILLED_MAP) && stack.get(DataComponents.MAP_ID) != null) {
            callbackInfo.setReturnValue(Optional.of(new MaptipTooltipData(stack.copy())));
            callbackInfo.cancel();
        }
    }
}
