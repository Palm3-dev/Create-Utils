package com.palm3.createutils.foundation.mixins;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour")
public class ScrollValueBehaviourMixin {

    @Inject(
            method = "onShortInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult, CallbackInfo ci) {
        if (CUMain.shouldDisableScrollValues() && player.getItemInHand(hand).getItem() instanceof ScrewdriverItem screwdriverItem) {
            screwdriverItem.useOn(new UseOnContext(player, hand, hitResult));
            ci.cancel();
        }
    }
}
