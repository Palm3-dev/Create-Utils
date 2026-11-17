package com.palm3.createutils.content.items.screwdriver;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.config.CUCommonConfig;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.event.InputEvent;

import javax.annotation.Nullable;

public class ScrewdriverEvents {

    // Returns clicked block entity behaviour (KineticScrollValueBehaviour) if present, if fails returns 'null'.
    protected static @Nullable KineticScrollValueBehaviour getKSVB() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel cLevel = mc.level;
        LocalPlayer player = mc.player;
        if (player == null || cLevel == null)
            return null;

        ItemStack itemInHand = player.getItemInHand(player.getUsedItemHand());

        if (!(itemInHand.getItem() instanceof ScrewdriverItem))
            return null;

        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

        if (hitResult == null)
            return null;

        BlockPos lookingPos = hitResult.getBlockPos();
        BlockEntity lookedBE = cLevel.getBlockEntity(lookingPos);

        if (!(lookedBE instanceof SmartBlockEntity sbe))
            return null;

        return (KineticScrollValueBehaviour) sbe.getBehaviour(KineticScrollValueBehaviour.TYPE);
    }

    // Changes rotation speed
    public static void onMouseScrollInWorld(InputEvent.MouseScrollingEvent event) {
        if (!CUCommonConfig.ENABLE_OLD_LIKE_SPEED_SETTINGS.getAsBoolean())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        KineticScrollValueBehaviour ksvb = getKSVB();

        if (ksvb == null || player == null)
            return;

        // Set the values in the KineticScrollValueBehaviour of the block entity ON THE CLIENT.
        // This is done to store the value and let the screwdriver item set it via server packet.
        // This because otherwise, too rapid changes to the speed could break the block, so the player
        // needs to confirm and set the value manually, otherwise in the next update of the BE the value will come back to previous value.
        int currentValue = ksvb.getValue();  // Actual currently set value, has sign.
        int currentAbsValue = Math.abs(currentValue);  // The value used for the speed change, packet only wants positive value.
        int step = player.isShiftKeyDown() ? 32 : 1;
        boolean positive = currentValue > 0;
        boolean increment = event.getScrollDeltaY() > 0;
        int newAbsValue = increment ? (currentAbsValue + step) : (currentAbsValue - step);
        newAbsValue = Math.max(1, (currentAbsValue == 1 && step == 32 ? newAbsValue - 1 : newAbsValue));  // To have from 1 to 32 instead of 1 to 33
        ksvb.setValue(positive ? newAbsValue : -newAbsValue);

        // Scroll sound
        float pitch = newAbsValue / (float) AllConfigs.server().kinetics.maxRotationSpeed.get();
        pitch = Mth.lerp(pitch, 1.15f, 1.5f);
        mc.getSoundManager().play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(), pitch, 0.25F));

        event.setCanceled(true);  // Avoids inventory slot change
    }

    // Changes rotation direction
    public static void onLeftClickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        if (!CUCommonConfig.ENABLE_OLD_LIKE_SPEED_SETTINGS.getAsBoolean())
            return;

        if (!event.isAttack())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        KineticScrollValueBehaviour ksvb = getKSVB();

        if (ksvb == null || player == null)
            return;

        int newValue = ksvb.getValue() * -1;
        ksvb.setValue(newValue);  // Invert direction

        CatnipServices.NETWORK.sendToServer(new ValueSettingsPacket(  // Update  //todo add delay or breaks most of the time
                ksvb.getPos(),
                newValue > 0 ? 1 : 0,
                Math.abs(newValue),
                null,
                null,
                Direction.UP,
                AllKeys.ctrlDown(),
                ksvb.netId()
        ));

        event.setCanceled(true);  // Disables block breaking
    }

    // Returns true if success
    public static boolean trySendUpdatePacket(BlockEntity clickedBE, Level level) {
        if (!CUCommonConfig.ENABLE_OLD_LIKE_SPEED_SETTINGS.getAsBoolean())
            return false;

        if (clickedBE instanceof SmartBlockEntity sbe && level.isClientSide) {  // Only do on client: since settings are applied by screwdriver only in client event, we get from there the values.
            KineticScrollValueBehaviour ksvb = (KineticScrollValueBehaviour) sbe.getBehaviour(KineticScrollValueBehaviour.TYPE);

            if (ksvb != null) {
                // Sends to the server the current KBE settings stored on the client to synchronize them.
                CatnipServices.NETWORK.sendToServer(new ValueSettingsPacket(
                        sbe.getBlockPos(),
                        ksvb.getValue() > 0 ? 1 : 0,  // As in KineticScrollValueBehaviour:39, raw index 0 is negative.
                        Math.abs(ksvb.getValue()),  // Requires absolute value, direction determined above.
                        null,
                        null,
                        Direction.UP,
                        AllKeys.ctrlDown(),
                        ksvb.netId()
                ));
                return true;
            }
        }
        return false;
    }
}
