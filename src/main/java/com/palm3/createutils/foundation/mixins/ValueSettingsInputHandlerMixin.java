package com.palm3.createutils.foundation.mixins;

import com.palm3.createutils.foundation.items.ksvb_modifier.IKSVBModifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Disables the scroll value screen opening when condition are met (see IKSVBModifier.canDisableScroll()).

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler")
public abstract class ValueSettingsInputHandlerMixin {

    @Inject(
            method = "canInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void canInteract(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (IKSVBModifier.canDisableScroll()) {
            cir.setReturnValue(false);
        }
    }
}
