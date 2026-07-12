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
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import static com.palm3.createutils.content.blocks.smarter_observer.SOSettingsRepresenter.PropertiesRepresenter.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
public class SmarterObserverBlockEntity extends SmartBlockEntity {

    // Filtering targets
    public Block targetBlock = Blocks.AIR;  // Default to air (no filter). Has no properties.
    public String targetProperty = PropertiesRepresenter.NOT_SET;  // Set with target block
    public String targetValue = PropertiesRepresenter.NOT_SET;  // Set with target block

    // Behaviour settings
    public String detectMode = DetectModeRepresenter.DETECT_BOTH;  // Used by screen to know (when block placed for the first time) what to put in button icon.
    public Integer onForTicks = 2;  // Should be 1 redstone tick, i think? Used by screen to know (when block placed for the first time) what to put in scroll input.
    public BlockState previousBlockStateInFront = Blocks.AIR.defaultBlockState();
    public String blockRemovedDetectingPropsBehaviour = BlockRemovedDetectingPropsBehavioursRepresenter.LOCKED_FOR_NO_PROPS;  // Default to this since at first startup no properties are selected, always.

    // Screen settings
    public boolean showOnlyTicks = true;


    // Tags names
    private static final String targetBlock_Tag = "target_block";
    private static final String targetProperty_Tag = "target_property";
    private static final String targetValue_Tag = "target_prop_value";
    private static final String detectMode_Tag = "detect_mode";
    private static final String onForTicks_Tag = "on_for_ticks";
    private static final String previousBlockStateInFront_Tag = "prev_block_state_in_front";
    private static final String blockRemovedDetectingPropsBehaviour_Tag = "block_rem_detect_props_bh";
    // Screen settings
    private static final String showOnlyTicks_Tag = "screen_show_only_ticks";


    // Block Filter
    private FilteringBehaviour filteringBehaviour;


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
            // Filter values setting.
            if (getTargetBlockProps().isEmpty()) {  // No props and thus values.
                targetProperty = PropertiesRepresenter.CANT_DETECT;
                targetValue = PropertiesRepresenter.CANT_DETECT;
            } else {
                targetProperty = PropertiesRepresenter.DONT_DETECT;
                targetValue = PropertiesRepresenter.DONT_DETECT;
            }
            blockRemovedDetectingPropsBehaviour = BlockRemovedDetectingPropsBehavioursRepresenter.LOCKED_FOR_NO_PROPS;  // In both cases, the filters aren't set.
            if (CUCommonConfig.logSmarterObserver()) CUMain.LOGGER.info("Selected filter: {}", Block.byItem(is.getItem()));
            this.setChanged();
        });
        behaviours.add(filteringBehaviour);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putString(targetBlock_Tag, BuiltInRegistries.BLOCK.getKey(targetBlock).toString());
        tag.putString(targetProperty_Tag, targetProperty);
        tag.putString(targetValue_Tag, targetValue);
        tag.putString(detectMode_Tag, detectMode);
        tag.putInt(onForTicks_Tag, onForTicks);
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, previousBlockStateInFront)
                        .result()
                        .ifPresent(blockStateTag -> tag.put(previousBlockStateInFront_Tag, blockStateTag));
        tag.putString(blockRemovedDetectingPropsBehaviour_Tag, blockRemovedDetectingPropsBehaviour);
        // Screen settings
        tag.putBoolean(showOnlyTicks_Tag, showOnlyTicks);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.targetBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(tag.getString(targetBlock_Tag)));
        this.targetProperty = tag.getString(targetProperty_Tag);
        this.targetValue = tag.getString(targetValue_Tag);
        this.detectMode = tag.getString(detectMode_Tag);
        this.onForTicks = tag.getInt(onForTicks_Tag);
        if (tag.contains(previousBlockStateInFront_Tag)) {
            this.previousBlockStateInFront = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get(previousBlockStateInFront_Tag))
                    .result()
                    .orElseThrow();
        } else throw new IllegalStateException("The CompoundTag doesn't contain the tag key previousBlockStateInFront_Tag");
        this.blockRemovedDetectingPropsBehaviour = tag.getString(blockRemovedDetectingPropsBehaviour_Tag);
        // Screen settings
        this.showOnlyTicks = tag.getBoolean(showOnlyTicks_Tag);
    }

    protected Collection<Property<?>> getTargetBlockProps() {
        return targetBlock.getStateDefinition().getProperties();
    }

    protected Block getPreviousBlockInFront() {
        return previousBlockStateInFront.getBlock();
    }

    protected void setPreviousBlockStateInFront(LevelAccessor level, BlockPos posInFront) {
        previousBlockStateInFront = level.getBlockState(posInFront);
    }

    protected boolean hasTargetBlock() {
        return targetBlock != Blocks.AIR;
    }

    /**
     * Contains the possible property and relative property value states, such as {@link PropertiesRepresenter#DONT_DETECT}, {@link PropertiesRepresenter#CANT_DETECT}...
     */
    protected enum PropValuesStates {
        BOTH_NON_EXISTENT_AND_NO_TARGET_BLOCK,
        BOTH_NON_EXISTENT,  // Target property and value non-existent.
        BOTH_NOT_SELECTED,
        ONLY_VALUE_SET,
        ONLY_PROPERTY_SET,
        BOTH_SELECTED;

        PropValuesStates() {}
    }

    /**
     * @return The current configuration of the property and property value as {@link PropValuesStates}. The actual values of the property/value are ignored.
     */
    protected @NotNull SmarterObserverBlockEntity.PropValuesStates getTargetPropAndValueConfiguration() {
        PropValuesStates propValuesStates;
        if ((targetProperty.equals(CANT_DETECT) && !targetValue.equals(targetProperty)) || (targetValue.equals(CANT_DETECT) && !targetProperty.equals(targetValue)))
            throw new IllegalStateException("Non-consistent target property and value configuration, both need to be CANT_DETECT if one of the two is. " +
                    "Current values (prop, value): " + targetProperty + " | " + targetValue);

        if (targetProperty.equals(NOT_SET) && targetBlock == Blocks.AIR) propValuesStates = PropValuesStates.BOTH_NON_EXISTENT_AND_NO_TARGET_BLOCK;
        else if (targetProperty.equals(NOT_SET)) throw new IllegalStateException("Target block should be AIR if the property is NOT_SET.");
        else if (targetProperty.equals(CANT_DETECT)) propValuesStates = PropValuesStates.BOTH_NON_EXISTENT;  // Also value is cant detect.
        else if (targetProperty.equals(DONT_DETECT) && targetValue.equals(DONT_DETECT)) propValuesStates = PropValuesStates.BOTH_NOT_SELECTED;
        else if (targetProperty.equals(DONT_DETECT)) propValuesStates = PropValuesStates.ONLY_VALUE_SET;
        else if (targetValue.equals(DONT_DETECT)) propValuesStates = PropValuesStates.ONLY_PROPERTY_SET;
        else propValuesStates = PropValuesStates.BOTH_SELECTED;

        return propValuesStates;
    }

    /**
     * @return 'true' if the BE has the property filter set.
     */
    protected boolean hasTargetProperty() {
        return !targetProperty.equals(DONT_DETECT) && !targetProperty.equals(CANT_DETECT);
    }

    /**
     * @return 'true' if the BE has the property value set.
     */
    protected boolean hasTargetValue() {
        return !targetValue.equals(DONT_DETECT) && !targetValue.equals(CANT_DETECT);
    }

    /**
     * @return 'true' if the BE has the property and the property value set.
     */
    protected boolean hasTargetPropAndValue() {
        return hasTargetProperty() && hasTargetValue();
    }
}
