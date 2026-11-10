package com.palm3.createutils.content.adjustable_speed_controller.compact;

import com.palm3.createutils.register.CUBlockEntities;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdjustableSpeedControllerCompactBlock extends AbstractEncasedShaftBlock implements IBE<SplitShaftBlockEntity>  {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public AdjustableSpeedControllerCompactBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(POWER, context.getLevel().getBestNeighborSignal(context.getClickedPos()));
    }

    @Override
    public Class<SplitShaftBlockEntity> getBlockEntityClass() {
        return SplitShaftBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CUBlockEntities.ADJ_SPEED_CONTROLLER_BE_COMPACT.get();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;

        int power = level.getBestNeighborSignal(pos);

        if (state.getValue(POWER) != power) {
            level.setBlock(pos, state.setValue(POWER, power), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            withBlockEntityDo(level, pos, kbe -> ((AdjustableSpeedControllerCompactBlockEntity) kbe).onNeighbourChanged());
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction fromDirection) {
        Direction.Axis ascAxis = state.getValue(AXIS);
        if (fromDirection == null) return false;  // Fallback
        return switch (fromDirection) {
            case UP, DOWN -> ascAxis != Direction.Axis.Y;
            case NORTH, SOUTH -> ascAxis != Direction.Axis.Z;
            case EAST, WEST -> ascAxis != Direction.Axis.X;
        };
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (oldState.getBlock() == state.getBlock())
            return;
        withBlockEntityDo(worldIn, pos, kbe -> ((AdjustableSpeedControllerCompactBlockEntity) kbe).onNeighbourChanged());
    }
}