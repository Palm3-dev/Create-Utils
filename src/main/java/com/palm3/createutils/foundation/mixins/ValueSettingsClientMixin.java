package com.palm3.createutils.foundation.mixins;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Disables value settings screen opening

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient")
public abstract class ValueSettingsClientMixin {

    @Shadow private Minecraft mc;

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    public void tick(CallbackInfo ci) {
        if (CUMain.shouldDisableScrollValues())
            ci.cancel();  // Completely disables scroll behaviour screen opening.
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (CUMain.shouldDisableScrollValues())
            ci.cancel();  // Disables scroll behaviour infos (e.g. "Hold to modify").
    }
}
