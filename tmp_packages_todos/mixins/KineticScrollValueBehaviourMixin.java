package com.palm3.createutils.foundation.mixins.scroll_value;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import com.palm3.createutils.register.CUItems;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour")
public abstract class KineticScrollValueBehaviourMixin extends ScrollValueBehaviour {

    public KineticScrollValueBehaviourMixin(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Inject(
            method = "setValueSettings",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld, CallbackInfo ci) {
        if (player.getItemInHand(player.getUsedItemHand()).getItem() instanceof ScrewdriverItem) {  // Only do if instance of screwdriver item
            int absValue = 1;

            /*int value = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);*/
            setValue(getValue() < 0 ? -16 : 16);
            ci.cancel();
        }
    }
}