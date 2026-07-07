package com.palm3.createutils.content.blocks.smarter_observer;

import com.mojang.serialization.MapCodec;
import com.palm3.createutils.CUMain;
import com.palm3.createutils.register.CUBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SmarterObserverBlock extends Block implements EntityBlock, IBE<SmarterObserverBlockEntity> {
    /*
     * Detection is structured by layers; power actions determine time on, time off etc.:
     *
     * Block placed, filtered block?
     *              - NO --> power actions
     *              - YES ↓
     *                    Block matches filter?
     *                                - NO --> skip
     *                                - YES ↓
     *                                      Check property?
     *                                                - NO --> power actions
     *                                                - YES ↓
     *                                                      Property matches?
     *                                                                   - NO --> skip
     *                                                                   - YES ↓
     *                                                                         Check property value?
     *                                                                                        - NO --> power actions
     *                                                                                        - YES ↓
     *                                                                                              Property value matches?
     *                                                                                                           - NO --> skip
     *                                                                                                           - YES --> power actions
     */

    public static final MapCodec<SmarterObserverBlock> CODEC = simpleCodec(SmarterObserverBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public SmarterObserverBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false)
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null) {
            if (context.getPlayer().isShiftKeyDown())
                return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
            else
                return defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
        } else throw new IllegalArgumentException("Player cannot be null to check if pressing shift key!");
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmarterObserverBlockEntity(CUBlockEntities.SMARTER_OBSERVER_BE.get(), pos, state);
    }

    @Override
    public Class<SmarterObserverBlockEntity> getBlockEntityClass() {
        return SmarterObserverBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmarterObserverBlockEntity> getBlockEntityType() {
        return CUBlockEntities.SMARTER_OBSERVER_BE.get();
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        //withBlockEntityDo(level, pos, be -> SmarterObserverScreen.openScreen(be, level));  // OLD
        withBlockEntityDo(level, pos, be -> ScreenOpener.open(new SmarterObserverScreen(be)));
        return InteractionResult.CONSUME;
    }


    // Behaviour
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(FACING) == facing) {
            CUMain.LOGGER.info("THE CHANGE WAS IN FACING DIR.");
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        //
    }
}