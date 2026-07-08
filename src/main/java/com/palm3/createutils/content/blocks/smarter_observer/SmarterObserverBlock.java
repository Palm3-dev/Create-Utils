package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.register.CUBlockEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
public class SmarterObserverBlock extends DirectedDirectionalBlock implements EntityBlock, IBE<SmarterObserverBlockEntity> {
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
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty H_FACING = DirectedDirectionalBlock.FACING;

    public SmarterObserverBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false)
                .setValue(H_FACING, Direction.NORTH)
                .setValue(TARGET, AttachFace.WALL)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
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
            Direction lookDir = context.getNearestLookingDirection();
            if (context.getPlayer().isShiftKeyDown()) {
                return defaultBlockState()
                        .setValue(H_FACING, context.getHorizontalDirection())
                        .setValue(TARGET, lookDir == Direction.UP ? AttachFace.CEILING : lookDir == Direction.DOWN ? AttachFace.FLOOR : AttachFace.WALL);
            } else {
                return defaultBlockState()
                        .setValue(H_FACING, context.getHorizontalDirection().getOpposite())
                        .setValue(TARGET, lookDir == Direction.UP ? AttachFace.FLOOR : lookDir == Direction.DOWN ? AttachFace.CEILING : AttachFace.WALL);
            }
        } else throw new IllegalArgumentException("Player is null!");
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


    //------------------- BEHAVIOUR -------------------
    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() != AllItems.WRENCH.asItem()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            withBlockEntityDo(level, pos, be -> ScreenOpener.open(new SmarterObserverScreen(be)));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(H_FACING) == facing) {
            CUMain.LOGGER.info("THE CHANGE WAS IN H_FACING DIR.");
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        //
    }
}