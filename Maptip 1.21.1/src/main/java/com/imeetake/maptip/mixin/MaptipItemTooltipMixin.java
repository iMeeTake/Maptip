package com.imeetake.maptip.mixin;

import com.imeetake.maptip.tooltip.MaptipTooltipData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(Item.class)
public abstract class MaptipItemTooltipMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void maptip$injectTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if (stack.isOf(Items.FILLED_MAP) && stack.get(DataComponentTypes.MAP_ID) != null) {
            cir.setReturnValue(Optional.of(new MaptipTooltipData(stack.copy())));
            cir.cancel();
        }
    }
}