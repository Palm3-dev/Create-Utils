package com.palm3.createutils.content.blocks.smarter_observer;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.*;
import com.simibubi.create.content.redstone.FilteredDetectorFilterSlot;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import static com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlock.dl;
import static com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.SelectionRepresenter.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
public class SmarterObserverBlockEntity extends SmartBlockEntity {

    // Filtering targets
    public Block targetBlock = Blocks.AIR;  // Default to air (no filter). Has no properties.
    private static final String targetBlock_Tag = "target_block";
    public String targetProperty = SelectionRepresenter.NOT_SET;
    private static final String targetProperty_Tag = "target_property";
    public String targetValue = SelectionRepresenter.NOT_SET;
    private static final String targetValue_Tag = "target_prop_value";

    // Behaviour settings
    public String detectMode = DetectModeRepresenter.DETECT_BOTH;  // Used by screen to know (when block placed for the first time) what to put in button icon.
    private static final String detectModeTag = "detect_mode";
    public Integer onForTicks = 2;  // Should be 1 redstone tick, i think? Used by screen to know (when block placed for the first time) what to put in scroll input.
    private static final String onForTicksTag = "on_for_ticks";
    public Block previousBlockInFront = Blocks.AIR;
    private static final String previousBlockInFrontTag = "prev_block_front";

    // Screen settings
    public boolean showOnlyTicks = true;
    private static final String showOnlyTicksTag = "screen_show_only_ticks";

    // Block Filter
    private FilteringBehaviour filteringBehaviour;

    private String blockToString(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private Block stringToBlock(String blockLocation) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(blockLocation));
    }


    public SmarterObserverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        filteringBehaviour = new FilteringBehaviour(this, new FilteredDetectorFilterSlot(false));
        filteringBehaviour.setLabel(Component.translatable("smarter_observer_be.target_block"));
        filteringBehaviour.setFilter(new ItemStack(Blocks.AIR.asItem()));
        filteringBehaviour.showCountWhen(() -> false);
        filteringBehaviour.withCallback(is -> {
            targetBlock = Block.byItem(is.getItem());
            // Initialization of vars for the screen, needed to set the scroll values at first startup.
            if (getTargetBlockProps().isEmpty()) {  // No props and thus values.
                targetProperty = SelectionRepresenter.CANT_DETECT;
                targetValue = SelectionRepresenter.CANT_DETECT;
            } else {
                targetProperty = SelectionRepresenter.DONT_DETECT;
                targetValue = SelectionRepresenter.DONT_DETECT;
            }
            if (CUCommonConfig.LOG_ALL.getAsBoolean()) CUMain.LOGGER.info("Selected filter: {}", Block.byItem(is.getItem()));
            this.setChanged();
        });
        behaviours.add(filteringBehaviour);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putString(targetBlock_Tag, blockToString(targetBlock));
        tag.putString(targetProperty_Tag, targetProperty);
        tag.putString(targetValue_Tag, targetValue);
        tag.putString(detectModeTag, detectMode);
        tag.putInt(onForTicksTag, onForTicks);
        tag.putString(previousBlockInFrontTag, blockToString(previousBlockInFront));
        // Screen settings
        tag.putBoolean(showOnlyTicksTag, showOnlyTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.targetBlock = stringToBlock(tag.getString(targetBlock_Tag));
        this.targetProperty = tag.getString(targetProperty_Tag);
        this.targetValue = tag.getString(targetValue_Tag);
        this.detectMode = tag.getString(detectModeTag);
        this.onForTicks = tag.getInt(onForTicksTag);
        this.previousBlockInFront = stringToBlock(tag.getString(previousBlockInFrontTag));
        // Screen settings
        this.showOnlyTicks = tag.getBoolean(showOnlyTicksTag);
    }

    protected Collection<Property<?>> getTargetBlockProps() {
        return targetBlock.getStateDefinition().getProperties();
    }

    protected boolean shouldDetectProps() {
        return !targetProperty.equals(DONT_DETECT) && !targetProperty.equals(CANT_DETECT);
    }

    // Logs
    protected void logBeValues() {
        dl("All BlockEntity values:");
        dl(" - targetBlock: " + BuiltInRegistries.BLOCK.getKey(targetBlock).getNamespace() + ":" + BuiltInRegistries.BLOCK.getKey(targetBlock).getPath());
        dl(" - targetProperty: " + targetProperty);
        dl(" - targetValue: " + targetValue);
        dl(" - detectMode: " + detectMode);
        dl(" - onForTicks: " + onForTicks);
        dl(" - previousBlockInFront: " + BuiltInRegistries.BLOCK.getKey(previousBlockInFront).getNamespace() + ":" + BuiltInRegistries.BLOCK.getKey(previousBlockInFront).getPath());
    }


















    /*public void detectBlockProperties(BlockState detectorState, BlockPos detectorPos, LevelAccessor level) {
        if (detectorState.getBlock() instanceof SmarterObserverBlock && !level.isClientSide()) {
            BlockState soState = level.getBlockState(detectorPos);

            possibleProperties.clear();

            // Calculate the observed block pos based on facing direction.
            BlockPos observedBlockPos = switch (soState.getValue(FACING)) {
                case UP -> detectorPos.above();
                case DOWN -> detectorPos.below();
                case NORTH -> detectorPos.north();
                case SOUTH -> detectorPos.south();
                case EAST -> detectorPos.east();
                case WEST -> detectorPos.west();
            };

            BlockState observedBlockState = level.getBlockState(observedBlockPos);

            this.setChanged();

            // Add properties to given List<Property?>>
            possibleProperties.addAll(observedBlockState.getProperties());

            // Maybe print obtained properties and all their possible values
            if (CUCommonConfig.LOG_ALL.getAsBoolean()) {
                CUMain.LOGGER.info("List of all [{}] blockstate properties:", BuiltInRegistries.BLOCK.getKey(observedBlockState.getBlock()));
                possibleProperties.forEach(p -> {
                    CUMain.LOGGER.info("- Property '{}'", p.getName());
                    CUMain.LOGGER.info("    Possible values of property '{}': ", p.getName());
                    p.getPossibleValues().forEach(value -> CUMain.LOGGER.info("      {}", value.toString()));
                });
            }
        } else throw new IllegalArgumentException("Block needs to be instanceof SmarterObserverBlock!");
    }*/
}
