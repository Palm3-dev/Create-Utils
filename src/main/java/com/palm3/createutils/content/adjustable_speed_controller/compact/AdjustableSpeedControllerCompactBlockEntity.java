package com.palm3.createutils.content.adjustable_speed_controller.compact;

import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AdjustableSpeedControllerCompactBlockEntity extends SplitShaftBlockEntity {

    private int powerLevel;

    protected ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> outputDirection;  // Same enum as WindmillBlockEntity.
    private boolean outDirectionClockwise = true;  // Default value from scroll value is true.

    public AdjustableSpeedControllerCompactBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            // Input side, always 1f or else block breaks.
            if (face == getSourceFacing()) {
                return 1f;
            }

            // Output side and powered
            if (powerLevel > 0 && face == getSourceFacing().getOpposite()) {
                boolean clockwiseInput = getSpeed() > 0;
                boolean couldChangeDir = clockwiseInput != outDirectionClockwise;
                float outSpeedModifier = calcOutModifier(false, powerLevel);
                return couldChangeDir ? outSpeedModifier * -1 : outSpeedModifier;
            }
        }
        return 0f;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        outputDirection = new ScrollOptionBehaviour<>(
                WindmillBearingBlockEntity.RotationDirection.class,
                Component.translatable("block.create_utils.adjustable_speed_controller_compact.output_rotation_direction"),
                this,
                new ValueBoxTransform.Sided() {
                    @Override
                    protected Vec3 getSouthLocation() {
                        return VecHelper.voxelSpace(8, 8, 15.5);
                    }

                    @Override
                    protected boolean isSideActive(BlockState state, Direction direction) {
                        Direction.Axis ascAxis = state.getValue(AdjustableSpeedControllerCompactBlock.AXIS);
                        return switch (direction) {
                            case UP, DOWN -> ascAxis != Direction.Axis.Y;
                            case NORTH, SOUTH -> ascAxis != Direction.Axis.Z;
                            case EAST, WEST -> ascAxis != Direction.Axis.X;
                        };
                    }
                }
        );
        outputDirection.withCallback(i -> {
            outDirectionClockwise = (i == 0);
            updateKBE();
        });
        behaviours.add(outputDirection);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        onNeighbourChanged();
    }

    @Override
    public void tick() {
        super.tick();
        onNeighbourChanged();
    }

    /* Calculates the output speed modifier.
     *  boolean parameter decides the behaviour:
     *    - if true: when power is max, the output is max - (max / 16), minimum level is max / 16
     *    - if false: when power is max, the output is max, minimum level is 2 * (max / 16)
     *  Can't have both since that would require an additional power level (0-16, thus 17 total levels).
     */
    @SuppressWarnings("all")
    protected float calcOutModifier(boolean doMinimumStep, int rdstPower) {
        return (doMinimumStep ? rdstPower : rdstPower + 1) / 16f;
    }

    protected void onNeighbourChanged() {
        if (level == null || level.isClientSide)
            return;

        int power = level.getBestNeighborSignal(worldPosition);
        if (power != powerLevel) {
            powerLevel = power;
            setChanged();
            updateKBE();
        }
    }

    protected void updateKBE() {
        detachKinetics();
        removeSource();
        attachKinetics();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("PowerLevel", powerLevel);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        powerLevel = compound.getInt("PowerLevel");
        super.read(compound, registries, clientPacket);
    }
}