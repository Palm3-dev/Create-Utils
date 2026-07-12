package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.BlockRemovedDetectingPropsBehavioursRepresenter;
import com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.DetectModeRepresenter;
import com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.PropertiesRepresenter;
import com.palm3.createutils.register.CUBlockEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

/**
 * @deprecated Class is deprecated since it's a backup of {@link SmarterObserverBlock} done to avoid having to rollback from GitHub multiple commits behind.
 */
@Deprecated
@ParametersAreNonnullByDefault
public class SmarterObserverBlock_BACKUP extends DirectedDirectionalBlock implements EntityBlock, IBE<SmarterObserverBlockEntity> {

    protected static void dl(String msg) {
        if (CUCommonConfig.LOG_ALL.getAsBoolean() || CUCommonConfig.LOG_SMARTER_OBSERVER.getAsBoolean())
            CUMain.LOGGER.info("[SmarterObserver]: {}", msg);
    }
    private static String getBName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getNamespace() + ":" + BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    /*
     * Detection is structured by layers; power actions determine time on, time off etc.:
     *
     * Block placed, filtered block ()?
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

    public SmarterObserverBlock_BACKUP(Properties props) {
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
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
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

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    // Redstone properties
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
        // Direction parameter 'side' is the side of the placed redstone wire block that is trying to connect to this block.
        return side != getTargetDirection(state).getOpposite();
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        // The Direction parameter 'side' indicates in which direction for the requesting block (block that called getSignal()) this block is.
        return state.getValue(POWERED) && getTargetDirection(state).getOpposite() != side ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getSignal(level, pos, side);  // Same as getSignal()
    }

    // Behaviour
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState rotated = getRotatedBlockState(state, context.getClickedFace());

        level.setBlock(pos, rotated, 2);  // Doesn't trigger updates

        if (level.getBlockState(pos) != state)
            IWrenchable.playRotateSound(level, pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        Item heldItem = player.getItemInHand(hand).getItem();
        if (heldItem == AllItems.WRENCH.asItem()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;  // Do onWrenched()
        if (heldItem != Items.DEBUG_STICK) {
            withBlockEntityDo(level, pos, be -> ScreenOpener.open(new SmarterObserverScreen(be)));
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.SUCCESS;
    }

    private void startSignal(LevelAccessor level, BlockPos pos) {
        if (!level.isClientSide() && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction changedDir, BlockState changedState, LevelAccessor level, BlockPos thisBlockPos, BlockPos changedPos) {
        if (level.isClientSide()) return super.updateShape(state, changedDir, changedState, level, thisBlockPos, changedPos);

        if (changedDir == getTargetDirection(state)) {
            BlockEntity be = level.getBlockEntity(thisBlockPos);
            if (be instanceof SmarterObserverBlockEntity sobe) {
                Block changedBlock = changedState.getBlock();
                Collection<Property<?>> changedBlockProps = changedBlock.getStateDefinition().getProperties();
                PropertiesRepresenter changedB = new PropertiesRepresenter(changedBlockProps);

                if (sobe.targetBlock == null) throw new IllegalStateException("Target block (from BE) is null!");

                dl("");
                dl("");
                //sobe.logBeValues();  // non existing anymore

                switch (sobe.detectMode) {
                    // BOTH
                    case DetectModeRepresenter.DETECT_BOTH -> {
                        dl("");
                        dl("========= DETECT BOTH =========");

                        if (sobe.targetBlock == Blocks.AIR) {
                            dl("No target block specified, STARTING signal (no filters to check).");
                            this.startSignal(level, thisBlockPos);
                        }

                        else {
                            dl("Target block specified [" + getBName(sobe.targetBlock) + "]");

                            if (!sobe.hasTargetProperty()) {
                                dl("Target block has no props OR no selected property to filter.");
                                if (changedBlock != sobe.getPreviousBlockInFront()) {
                                    dl("Changed block isn't the same as the previous (not a property change)");
                                    if (changedBlock == sobe.targetBlock || (changedBlock == Blocks.AIR && sobe.getPreviousBlockInFront() == sobe.targetBlock)) {
                                        dl("Changed block is the target one (placed)   OR   {changed block is air and previous one is target block} (removed), STARTING signal");
                                        dl("Saved new currently placed block [" + getBName(changedBlock) + "] in BE in previousBlockInFront");
                                        sobe.setPreviousBlockStateInFront(level, changedPos);
                                        this.startSignal(level, thisBlockPos);

                                    } else dl("Changed block isn't the target one (placed)   OR   {changed block isn't air and/or previous one isn't target block} (removed), STARTING signal");
                                } else dl("Changed block IS the same as the previous (property change / same block in front)");
                            }

                            else {

                                if (sobe.hasTargetValue()) {
                                    dl("Target block has selected properties and values to filter.");
                                    if (changedBlock == Blocks.AIR && sobe.getPreviousBlockInFront() == sobe.targetBlock) {
                                        switch (sobe.blockRemovedDetectingPropsBehaviour) {
                                            case BlockRemovedDetectingPropsBehavioursRepresenter.DETECT_BLOCK_CHANGE -> {
                                                dl("Changed block is air AND previous is target (removed), no previous props to check, STARTING signal");
                                                sobe.setPreviousBlockStateInFront(level, changedPos);
                                                dl("Saved new currently placed block [" + getBName(changedBlock) + "] in BE in previousBlockInFront");
                                                this.startSignal(level, thisBlockPos);

                                            }
                                            case BlockRemovedDetectingPropsBehavioursRepresenter.DETECT_ONLY_PROPS -> {
                                                dl("Changed block is air AND previous is target (removed), now checking previous props");
                                                PropertiesRepresenter sr = new PropertiesRepresenter(sobe.getPreviousBlockInFront().getStateDefinition().getProperties());
                                                if (sr.hasProp(sobe.targetProperty) && sr.hasPValue(sobe.targetProperty, sobe.targetValue)) {
                                                    dl("Previous block contains target prop and prop value (doesn't mean it's in the correct state, only that it could have the values!)");
                                                    if (sobe.previousBlockStateInFront.getValue(sr.getProp(sobe.targetProperty)).toString().equals(sobe.targetValue)) {
                                                        dl("Previous block prop and value matches the target ones, STARTING signal.");
                                                        this.startSignal(level, thisBlockPos);
                                                    } else dl("Previous block prop/value doesn't match target.");
                                                } else dl("Previous block DOESN'T contain target prop and prop value");
                                                sobe.setPreviousBlockStateInFront(level, changedPos);
                                                dl("Saved new currently placed block [" + getBName(changedBlock) + "] in BE in previousBlockInFront");

                                            }
                                            case SOSettingsRepresenter.BlockRemovedDetectingPropsBehavioursRepresenter.LOCKED_FOR_NO_PROPS -> throw new IllegalStateException(SOSettingsRepresenter.BlockRemovedDetectingPropsBehavioursRepresenter.LOCKED_FOR_NO_PROPS + " Shouldn't reach here!");
                                            default -> throw new IllegalStateException("The BE value of blockRemovedDetectingPropsBehaviour [" + sobe.blockRemovedDetectingPropsBehaviour + "] doesn't exist!");
                                        }
                                    }

                                    if (changedB.hasProperties && changedBlock == sobe.targetBlock) {
                                        dl("Changed block is the target one AND has props (in general)");
                                        if (changedB.hasProp(sobe.targetProperty) && changedB.hasPValue(sobe.targetProperty, sobe.targetValue)) {
                                            dl("Changed block contains target prop and prop value (doesn't mean it's in the correct state, only that it could have the values!)");
                                            if (changedState.getValue(changedB.getProp(sobe.targetProperty)).toString().equals(sobe.targetValue)) {
                                                dl("Changed block prop and value matches the target ones, STARTING signal.");
                                                this.startSignal(level, thisBlockPos);
                                            } else dl("Changed block prop/value doesn't match target.");
                                        } else dl("Changed block DOESN'T contain target prop and prop value");
                                        sobe.setPreviousBlockStateInFront(level, changedPos);
                                        dl("Saved new currently placed block [" + getBName(changedBlock) + "] in BE in previousBlockInFront");
                                    }
                                }

                                // No value selected
                                else {
                                    //
                                }

                            }
                        }
                    }

                    // PLACED
                    case DetectModeRepresenter.DETECT_PLACED -> {
                        dl("detect placed, TODO");
                    }

                    // REMOVED
                    case DetectModeRepresenter.DETECT_REMOVED -> {
                        dl("detect removed, TODO");
                    }

                    default ->
                            throw new IllegalStateException("The current BE setting 'detectMode' [" + sobe.detectMode + "] is invalid and should not exist!");
                }

            } else throw new IllegalStateException("The BlockEntity at pos " + thisBlockPos + " is not a SmarterObserverBlockEntity!");
        }

        return super.updateShape(state, changedDir, changedState, level, thisBlockPos, changedPos);  // No changes made here
    }

    protected enum BlockAction {
        TARGET_REMOVED,
        TARGET_PLACED,
        OTHER_REMOVED,
        OTHER_PLACED,
        TARGET_BLOCK_PROP_CHANGE,
        OTHER_BLOCK_PROP_CHANGE;

        BlockAction() {}
    }

    /**
     * @deprecated Not correctly implemented in this class.
     */
    @Deprecated
    protected @NotNull BlockAction getBlockActionAndUpdatePreviousBS(Block changedBlock, BlockPos changedBlockPos, SmarterObserverBlockEntity sobe, LevelAccessor level) {
        BlockAction blockAction;
        // Block removed
        if (changedBlock == Blocks.AIR) {
            if (sobe.getPreviousBlockInFront() == sobe.targetBlock) blockAction = BlockAction.TARGET_REMOVED;
            if (sobe.getPreviousBlockInFront() != sobe.targetBlock) blockAction = BlockAction.OTHER_REMOVED;
        }

        // State changed
        if (changedBlock == sobe.getPreviousBlockInFront()) {
            if (changedBlock == sobe.targetBlock) blockAction = BlockAction.TARGET_BLOCK_PROP_CHANGE;
            else blockAction = BlockAction.OTHER_BLOCK_PROP_CHANGE;

        } else {  // Block changed
            if (changedBlock == sobe.targetBlock) blockAction = BlockAction.TARGET_PLACED;
            else blockAction = BlockAction.OTHER_PLACED;
        }

        sobe.setPreviousBlockStateInFront(level, changedBlockPos);  // Update to new changed blockstate
        return blockAction;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SmarterObserverBlockEntity sobe) {
            if (state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, false), 2);
                dl("Turned OFF");
            } else {
                level.setBlock(pos, state.setValue(POWERED, true), 2);
                level.scheduleTick(pos, this, sobe.onForTicks);
                dl("Turned ON for " + sobe.onForTicks + " ticks");
            }
            this.updateNeighbors(pos, state, level);
        }
    }

    private void updateNeighbors(BlockPos thisBlockPos, BlockState thisBlockState, Level level) {
        BlockPos oppositeToFront = thisBlockPos.relative(getTargetDirection(thisBlockState));  // Block in front of the face of the observer, not update.
        level.updateNeighborsAtExceptFromFacing(thisBlockPos, this, getTargetDirection(thisBlockState));

        // vanilla
        //level.neighborChanged(oppositeToFront, this, thisBlockPos);  // Updates block behind this (the one receiving the redstone pulse)
        //level.updateNeighborsAtExceptFromFacing(oppositeToFront, this, getTargetDirection(thisBlockState));  // Updates blocks around block behind this, except this block.
    }
}