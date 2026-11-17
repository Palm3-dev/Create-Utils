package com.palm3.createutils.foundation.mixins.scroll_value;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour")
public abstract class KineticScrollValueBehaviourMixin_OLD_TEMP extends ScrollValueBehaviour {

    public KineticScrollValueBehaviourMixin_OLD_TEMP(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Inject(
            method = "createBoard",
            at = @At("TAIL"),
            cancellable = true
    )
    public void createBoard(Player player, BlockHitResult hitResult, CallbackInfoReturnable<ValueSettingsBoard> cir,
                            @Local(name = "rows") ImmutableList<Component> rows, @Local(name = "formatter") ValueSettingsFormatter formatter) {
        int maxSpeed = AllConfigs.server().kinetics.maxRotationSpeed.get();
        //int milestoneInterval = (maxSpeed + 8) / 8;
        //LogUtils.getLogger().info("milestoneInterval: {}", milestoneInterval);
        cir.setReturnValue(new ValueSettingsBoard(label, maxSpeed, 32, rows, formatter));

        // milestoneInterval defines value of every milestone, just changes the number of milestones
    }
}