package com.palm3.createutils.foundation.mixins;

import com.palm3.createutils.foundation.items.IBlockFilterSelectorItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Predicate;

// Allows to items implementing IBlockFilterSelectorItem to override normal filter settings and set the item saved filter.

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour")
public abstract class FilteringBehaviourMixin extends BlockEntityBehaviour {
    @Shadow private Predicate<ItemStack> predicate;
    @Shadow protected FilterItemStack filter;
    @Shadow public int count;
    @Shadow public boolean upTo;
    @Shadow private Consumer<ItemStack> callback;

    public FilteringBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }

    @Inject(
            method = "setFilter(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setFilter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack is = stack.copy();
        if (!is.isEmpty() && is.getItem() instanceof IBlockFilterSelectorItem bfsi) {
            ItemStack actualFilter = bfsi.getTargetedItemStack(is);  // Itemstack 'is' implements IBlockFilterSelectorItem here.

            // Checks equals to original method
            if (!actualFilter.isEmpty() && !predicate.test(actualFilter)) {  // Check after get from, dev-note: here i think we could also disable the predicate? maybe?
                cir.setReturnValue(false);
            } else {
                filter = FilterItemStack.of(actualFilter);
                // Count check (as original method) not necessary here since bfsi.getTargetedItemStack(is) size is 1.
                callback.accept(actualFilter);
                blockEntity.setChanged();
                blockEntity.sendData();
                cir.setReturnValue(true);
            }
        }
    }
}
