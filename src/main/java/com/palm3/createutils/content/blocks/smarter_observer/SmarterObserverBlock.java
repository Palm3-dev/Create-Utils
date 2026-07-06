package com.palm3.createutils.content.blocks.smarter_observer;

import com.mojang.serialization.MapCodec;
import com.palm3.createutils.CUBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    /* *
     * Detect properties of the block in front of the facing direction (if facing north, the block north of this block)
     * and saves them in the HashMap present at the currentBlockPos block entity, that should be instanceof {@link SmarterObserverBlockEntity},
     * by also clearing it before saving the new properties.
     * @param currentBlockState The blockstate of the current block, used to know current facing direction.
     * @param currentBlockPos The position of the current block, to get the {@link BlockEntity} at that pos.
     * @param currentLevel The current level.
     * @param printLogs If logs should be printed.
     * /
    protected static void detectBlockProps(BlockState currentBlockState, BlockPos currentBlockPos, LevelAccessor currentLevel, HashMap<Integer, Property<?>> map, boolean printLogs) {
        //if (!currentLevel.isClientSide() && currentLevel.getBlockEntity(currentBlockPos) instanceof SmarterObserverBlockEntity sobe) {
        map.clear();

        // Calculate the observed block pos based on facing direction.
        BlockPos observedBlockPos = switch (currentBlockState.getValue(FACING)) {
            case UP -> currentBlockPos.above();
            case DOWN -> currentBlockPos.below();
            case NORTH -> currentBlockPos.north();
            case SOUTH -> currentBlockPos.south();
            case EAST -> currentBlockPos.east();
            case WEST -> currentBlockPos.west();
        };

        BlockState observedBlockState = currentLevel.getBlockState(observedBlockPos);

        // Save observed Block
        //sobe. = observedBlockState.getBlock();

        // Add properties to given HashMap
        int mapStateIndex = 0;
        for (Property<?> p : observedBlockState.getProperties()) {
            map.put(mapStateIndex, p);
            mapStateIndex++;
        }

        // Maybe print obtained properties and all their possible values
        if (printLogs) {
            CUMain.LOGGER.info("List of all [{}] blockstate properties:", BuiltInRegistries.BLOCK.getKey(observedBlockState.getBlock()));
            map.forEach((i, p) -> {
                CUMain.LOGGER.info("- Property '{}', saved at HashMap index {}", p.getName(), i);
                CUMain.LOGGER.info("    Possible values of property '{}': ", p.getName());
                p.getPossibleValues().forEach(value -> CUMain.LOGGER.info("      {}", value.toString()));
            });
        }

            /*
            // Update block entity
            sobe.setChanged();
        } else {
            String levelType = currentLevel.isClientSide() ? "CLIENT." : "SERVER.";
            String beType = currentLevel.getBlockEntity(currentBlockPos) instanceof SmarterObserverBlockEntity
                    ? " BlockEntity at " + currentBlockPos + " IS instanceof SmarterObserverBlockEntity"
                    : " BlockEntity at " + currentBlockPos + " IS NOT instanceof SmarterObserverBlockEntity";
            throw new IllegalArgumentException("Level type: " + levelType + beType);
        }* /
    }

    private static HashMap<Integer, Property<?>> detectBlockPropsAsMap(BlockState currentBlockState, BlockPos currentBlockPos, LevelAccessor currentLevel, boolean printLogs) {
        HashMap<Integer, Property<?>> map = new HashMap<>();
        detectBlockProps(currentBlockState, currentBlockPos, currentLevel, map, printLogs);
        return map;
    }*/

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        withBlockEntityDo(level, pos, be -> SmarterObserverScreen.openScreen(be, level));
        return InteractionResult.CONSUME;
    }





    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
    }



    /*
    private static boolean observedBlockChanged(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() != observedBlock;
    }

    private static boolean observedPropsChanged(BlockState currentBlockState, BlockPos currentBlockPos, LevelAccessor level) {
        return !detectBlockPropsAsMap(currentBlockState, currentBlockPos, level, false).equals(observedBlockProperties);
    }*/




    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
       /* if (!level.isClientSide()) {
            if (observedBlockChanged(level, facingPos)) {
                CUMain.LOGGER.info("BLOCK HAS CHANGED!");
                CUMain.LOGGER.info("Updating observedBlockProperties");
                detectBlockProps(state, currentPos, level, observedBlockProperties, CUCommonConfig.LOG_ALL.getAsBoolean());
            } else if (observedPropsChanged(state, currentPos, level)) { // confronitng props, not props values todo fix ts
                CUMain.LOGGER.info("BLOCK property(es) HAS CHANGED!");
                //fire event things
            } else if (observedBlockChanged(level, facingPos) && observedPropsChanged(state, currentPos, level)) {
                CUMain.LOGGER.info("props and block changed");
                CUMain.LOGGER.info("Updating observedBlockProperties");
                detectBlockProps(state, currentPos, level, observedBlockProperties, CUCommonConfig.LOG_ALL.getAsBoolean());
            }
            CUMain.LOGGER.info("");
        }*/

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }


}