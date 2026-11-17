package com.palm3.createutils.foundation.mixins;

import com.palm3.createutils.content.blocks.adjustable_speed_controller.full.AdjustableSpeedControllerFullBlock;
import com.palm3.createutils.content.blocks.adjustable_speed_controller.full.AdjustableSpeedControllerFullBlockEntity;
import com.palm3.createutils.register.CUBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.simibubi.create.content.kinetics.RotationPropagator")
public class RotationPropagatorSpeedControllerMixin {

    @Inject(method = "getConveyedSpeed", at = @At("HEAD"), cancellable = true)
    private static void getConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to, CallbackInfoReturnable<Float> cir) {
        final BlockState fromBS = from.getBlockState();
        final BlockState toBS = to.getBlockState();

        // Adjustable Speed Controller Full <-> Large Gear
        if (createutils$isLargeCogToAdjustableSpeedControllerFull(fromBS, toBS, to.getBlockPos().subtract(from.getBlockPos())))
            cir.setReturnValue(AdjustableSpeedControllerFullBlockEntity.getConveyedSpeed(from, to, true));
        if (createutils$isLargeCogToAdjustableSpeedControllerFull(fromBS, toBS, from.getBlockPos().subtract(to.getBlockPos())))
            cir.setReturnValue(AdjustableSpeedControllerFullBlockEntity.getConveyedSpeed(to, from, false));
    }

    @Unique
    @SuppressWarnings("all")
    private static boolean createutils$isLargeCogToAdjustableSpeedControllerFull(BlockState from, BlockState to, BlockPos diff) {
        if (!ICogWheel.isLargeCog(from) || !CUBlocks.ADJ_SPEED_CONTROLLER_FULL.has(to))
            return false;
        if (!diff.equals(BlockPos.ZERO.below()))
            return false;
        Direction.Axis axis = from.getValue(CogWheelBlock.AXIS);
        if (axis.isVertical())
            return false;
        if (to.getValue(AdjustableSpeedControllerFullBlock.HORIZONTAL_AXIS) == axis)
            return false;
        return true;
    }
}
