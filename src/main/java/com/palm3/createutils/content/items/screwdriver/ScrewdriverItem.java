package com.palm3.createutils.content.items.screwdriver;

import com.mojang.blaze3d.vertex.PoseStack;
import com.palm3.createutils.foundation.items.IBlockFilterSelectorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.IItemDecorator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ScrewdriverItem extends Item implements IBlockFilterSelectorItem {

    // Shows selected target block filter
    public static final IItemDecorator DECORATOR = (guiGraphics, font, stack, xOffset, yOffset) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset + 8, 100);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        guiGraphics.renderItem(((ScrewdriverItem) stack.getItem()).getTargetedItemStack(stack), 16, 0);
        poseStack.popPose();
        return false;
    };

    public ScrewdriverItem(Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return IBlockFilterSelectorItem.setTargetBlockTo(player, usedHand, Blocks.AIR);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // Scroll value settings behaviour
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity clickedBE = level.getBlockEntity(clickedPos);
        if (ScrewdriverEvents.trySendUpdatePacket(clickedBE, level))
            return InteractionResult.SUCCESS;

        // Filter behaviour
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        return player.isShiftKeyDown() ? IBlockFilterSelectorItem.setTargetBlockTo(context, Blocks.AIR) : setTargetBlock(context);
    }
}
