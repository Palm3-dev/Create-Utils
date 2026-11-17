package com.palm3.createutils.foundation.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

// This interfaces allows items to store and select a block (or itemstack) to be used as filter.

@ParametersAreNonnullByDefault
public interface IBlockFilterSelectorItem {

    static @NotNull String readTag(ItemStack targetItemStack) {
        return targetItemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("TargetedBlock");
    }

    static void writeTag(ItemStack targetItemStack, String value) {
        CompoundTag tag = targetItemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putString("TargetedBlock", value);
        targetItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    static @NotNull InteractionResult setTargetBlockTo(UseOnContext context, Block block) {
        writeTag(context.getItemInHand(), BuiltInRegistries.BLOCK.getKey(block).toString());
        return InteractionResult.SUCCESS;
    }

    static @NotNull InteractionResultHolder<ItemStack> setTargetBlockTo(Player player, InteractionHand usedHand, Block block) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        writeTag(stackInHand, BuiltInRegistries.BLOCK.getKey(block).toString());
        return InteractionResultHolder.success(stackInHand);
    }

    // Sets the targeted Block to clicked one, designed for use in 'useOn()' method.
    default @NotNull InteractionResult setTargetBlock(UseOnContext context) {
        ItemStack stackInHand = context.getItemInHand();
        Block clickedBlock = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
        writeTag(stackInHand, BuiltInRegistries.BLOCK.getKey(clickedBlock).toString());
        return InteractionResult.SUCCESS;
    }

    // Takes ItemStack (that needs to be an instance of this interface) and returns targeted Block.
    default Block getTargetedBlock(ItemStack stack) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(readTag(stack)));
    }

    // Takes ItemStack (that needs to be an instance of this interface) and returns targeted Block as ItemStack of size 1.
    default ItemStack getTargetedItemStack(ItemStack stack) {
        return new ItemStack(getTargetedBlock(stack), 1);
    }
}
