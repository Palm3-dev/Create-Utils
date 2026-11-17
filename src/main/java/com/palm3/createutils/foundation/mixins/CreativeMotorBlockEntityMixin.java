package com.palm3.createutils.foundation.mixins;

import com.simibubi.create.infrastructure.config.AllConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Sets the max speed of the motor to the max network speed LIKE IT SHOULD BE, oh, come on guys!

@Mixin(targets = "com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity")
public class CreativeMotorBlockEntityMixin {

    @ModifyVariable(
            method = "addBehaviours",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "max"
    )
    private int modifyMaxSpeed(int originalMaxSpeed) {
        return AllConfigs.server().kinetics.maxRotationSpeed.get();
    }
}
