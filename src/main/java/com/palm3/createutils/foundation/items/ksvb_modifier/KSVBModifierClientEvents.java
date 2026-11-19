package com.palm3.createutils.foundation.items.ksvb_modifier;

import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

/*
    Events to handle implementors of IKSVBModifier.

    NOTE: value saves are disabled when player is shifting, to allow compatibility with IFilterSelector.
 */

@EventBusSubscriber(Dist.CLIENT)
public class KSVBModifierClientEvents {

    @SubscribeEvent
    public static void onMouseScrollInWorld(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ClientLevel cLevel = mc.level;

        if (player == null || cLevel == null)
            return;

        Item itemInHand = player.getItemInHand(player.getUsedItemHand()).getItem();

        if (!(itemInHand instanceof IKSVBModifier modifierItem))
            return;

        KineticScrollValueBehaviour ksvb = IKSVBModifier.getBehaviour();

        if (ksvb == null)
            return;

        IKSVBModifier.modifyScrollValueOnClient(modifierItem.getNewValue(new IKSVBModifier.ModifyContext(
                ksvb,
                player,
                event.getScrollDeltaY() > 0,
                player.isShiftKeyDown() ? 32 : 1
        )), true);

        event.setCanceled(true);  // Avoids inventory slot change
    }

    @SubscribeEvent
    public static void onLeftClickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ClientLevel cLevel = mc.level;

        if (player == null || cLevel == null)
            return;

        Item itemInHand = player.getItemInHand(player.getUsedItemHand()).getItem();

        if (!(itemInHand instanceof IKSVBModifier))
            return;

        if (player.getCooldowns().isOnCooldown(itemInHand)) {
            event.setCanceled(true);
            return;
        }

        IKSVBModifier.reverseDirection();

        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

        if (hitResult == null)
            return;

        BlockEntity clickedBE = cLevel.getBlockEntity(hitResult.getBlockPos());

        // Instantly update the server
        if (IKSVBModifier.tryUpdateServer(clickedBE, cLevel)) {
            player.getCooldowns().addCooldown(itemInHand, 5);  // Avoids multiple consecutive changes
            event.setCanceled(true);  // Avoids block breaking
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ClientLevel cLevel = mc.level;

        if (player == null || cLevel == null)
            return;

        // For compatibility with IFilterSelector implementors, otherwise there's no way to set filter on blocks with KineticScrollValueBehaviour.
        if (player.isShiftKeyDown())
            return;

        Item itemInHand = player.getItemInHand(player.getUsedItemHand()).getItem();

        if (!(itemInHand instanceof IKSVBModifier))
            return;

        if (player.getCooldowns().isOnCooldown(itemInHand))
            return;

        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

        if (hitResult == null)
            return;

        BlockEntity clickedBE = cLevel.getBlockEntity(hitResult.getBlockPos());

        if (IKSVBModifier.tryUpdateServer(clickedBE, cLevel)) {
            player.getCooldowns().addCooldown(itemInHand, 5);  // Avoids multiple consecutive changes
            event.setCanceled(true);  // Avoids block breaking
        }
    }
}
