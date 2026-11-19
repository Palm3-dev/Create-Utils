package com.palm3.createutils.foundation.items.ksvb_modifier;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
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

import javax.annotation.Nullable;

/*
    Name abbreviation for KineticScrollValueBehaviourModifier.
    It's an interface just for mixins and eventual addons compatibility,
    so that an addon item can just use this interface, and it's all automatically done.
*/
public interface IKSVBModifier {

    /**
     * Checks if the given {@link SmartBlockEntity} has the requested behaviour {@link KineticScrollValueBehaviour}.
     * <br><br>
     * This is used to ensure that the BE possesses that specific behaviour, since giving {@link KineticScrollValueBehaviour#TYPE} to {@link SmartBlockEntity#getBehaviour(BehaviourType)} returns
     * {@link com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour}, extended by other behaviour types,
     * and thus passing {@code behaviour == null} checks, even tho the BE doesn't have the specific required behaviour.
     * @param be The block entity to check.
     * @return {@code true} if the block entity has the behaviour, {@code false} if not.
     */
    static boolean hasBehaviour(SmartBlockEntity be) {
        boolean hasBehaviour = false;
        for (BlockEntityBehaviour b : be.getAllBehaviours()) {
            if (b instanceof KineticScrollValueBehaviour) {
                hasBehaviour = true;
                break;
            }
        }
        return hasBehaviour;
    }

    /**
     * @return the held {@link ItemStack} in player used hand. {@code null} if player is also null.
     */
    static @Nullable ItemStack getStackInHand() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return null;
        return player.getItemInHand(player.getUsedItemHand());
    }

    /**
     * @return the {@link KineticScrollValueBehaviour} of the block on the clicked position (by the player), that <b>can be</b> {@code null}.
     * Remember that it's all done on client here.
     */
    static @Nullable KineticScrollValueBehaviour getBehaviour() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel cLevel = mc.level;

        if (getStackInHand() == null || !(getStackInHand().getItem() instanceof IKSVBModifier))
            return null;

        if (player == null || cLevel == null)
            return null;

        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

        if (hitResult == null)
            return null;

        BlockPos lookingPos = hitResult.getBlockPos();
        BlockEntity lookedBE = cLevel.getBlockEntity(lookingPos);

        if (!(lookedBE instanceof SmartBlockEntity sbe))
            return null;

        if (!hasBehaviour(sbe))
            return null;

        return (KineticScrollValueBehaviour) sbe.getBehaviour(KineticScrollValueBehaviour.TYPE);
    }

    /**
     * Modifies the {@link KineticScrollValueBehaviour} value <b>on the client side.</b> No server synchronization is done!
     * @param newSignedValue The new value with sign (can be negative).
     * @param playSound If {@link AllSoundEvents#SCROLL_VALUE} sound should be played when value is modified.
     */
    static void modifyScrollValueOnClient(int newSignedValue, boolean playSound) {

        KineticScrollValueBehaviour ksvb = getBehaviour();

        if (ksvb == null)
            return;

        ksvb.setValue(newSignedValue);  // Client here only requires raw value, packet needs scroll value row (for direction) and absolute value.

        if (!playSound)
            return;

        // Scroll sound, formula from create code
        float pitch = Math.max(1, Math.abs(newSignedValue)) / (float) AllConfigs.server().kinetics.maxRotationSpeed.get();
        pitch = Mth.lerp(pitch, 1.15f, 1.5f);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(), pitch, 0.25F));
    }

    /**
     * Reverses the direction, only <b>on the client.</b>
     * Utility for method {@link #modifyScrollValueOnClient(int, boolean)}.
     * Doesn't play scroll value sound.
     */
    static void reverseDirection() {
        KineticScrollValueBehaviour ksvb = getBehaviour();
        if (ksvb == null)
            return;
        modifyScrollValueOnClient(ksvb.getValue() * -1, false);
    }

    /**
     * Updates the server by taking the <b>client side</b> value of the {@link KineticScrollValueBehaviour} of the clicked block entity.
     * @param clickedBE The clicked block entity, that should be instance of {@link SmartBlockEntity}, and also have the required behaviour.
     * @param level The current level. Call this method <b>before</b> level checks.
     * @return <ul>
     *     <li>{@code true} if the packed has been sent successfully (client -> server, method called on client).</li>
     *     <li>{@code true} if the method is called on the server side (skip).</li>
     *     <li>{@code false} if the packet send fails (for example the block entity doesn't have {@link KineticScrollValueBehaviour}).</li>
     *     <li>{@code false} if the used item is not instance of this interface.</li>
     * </ul>
     */
    static boolean tryUpdateServer(BlockEntity clickedBE, Level level) {
        if (getStackInHand() == null || !(getStackInHand().getItem() instanceof IKSVBModifier))
            return false;

        if (!(clickedBE instanceof SmartBlockEntity sbe))
            return false;

        if (level.isClientSide && hasBehaviour(sbe)) {  // Only do on client: since settings are applied by screwdriver only in client event, we get from there the values.
            KineticScrollValueBehaviour ksvb = (KineticScrollValueBehaviour) sbe.getBehaviour(KineticScrollValueBehaviour.TYPE);

            if (ksvb != null) {
                int currentValue = ksvb.getValue();
                // Sends to the server the current KBE settings stored on the client to synchronize them.
                CatnipServices.NETWORK.sendToServer(new ValueSettingsPacket(
                        sbe.getBlockPos(),
                        currentValue > 0 ? 1 : 0,  // As in KineticScrollValueBehaviour:39, raw index 0 is negative.
                        Math.abs(currentValue),  // Requires absolute value, direction determined above.
                        null,
                        null,
                        Direction.UP,
                        AllKeys.ctrlDown(),
                        ksvb.netId()
                ));
                return true;
            }
        } else return !level.isClientSide;
        return false;
    }

    /**
     * @param context The {@link ModifyContext}.
     * @return The new value to apply to the {@link KineticScrollValueBehaviour}, with sign.
     * By default, the increment/decrement step is {@code 32} when player (from context) is shifting, {@code 1} otherwise.
     */
    default int getNewValue(ModifyContext context) {
        KineticScrollValueBehaviour ksvb = context.ksvb();
        Player player = context.player();

        if (ksvb == null)
            throw new IllegalArgumentException("Cannot get new value because KineticScrollValueBehaviour is null!");

        if (player == null)
            throw new IllegalArgumentException("Cannot get new value because player is null!");

        int currentValue = ksvb.getValue();
        int currentAbsValue = Math.abs(currentValue);
        int step = context.step();
        boolean positive = currentValue > 0;
        int newAbsValue = context.increment() ? (currentAbsValue + step) : (currentAbsValue - step);
        newAbsValue = Math.max(1, (currentAbsValue == 1 && step == 32 ? newAbsValue - 1 : newAbsValue));  // To have from 1 to 32 instead of 1 to 33
        return positive ? newAbsValue : -newAbsValue;
    }

    /**
     * Contains values used to modify the scroll value.
     * @param ksvb The {@link KineticScrollValueBehaviour}.
     * @param player The player.
     * @param increment If the value should be incremented or decremented.
     * @param step The step applied to the speed (by how much the speed is increased/decreased).
     */
    record ModifyContext(KineticScrollValueBehaviour ksvb, Player player, boolean increment, int step) {}

    /**
     * @return {@code true} when the {@link KineticScrollValueBehaviour} can be disabled,
     * used in mixins with target {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler#canInteract(Player)}.
     */
    @SuppressWarnings("all")
    static boolean canDisableScroll() {
        if (getStackInHand() == null || !(getStackInHand().getItem() instanceof IKSVBModifier))
            return false;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel cLevel = mc.level;
        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;

        if (hitResult == null || cLevel == null)
            return false;

        BlockEntity clickedBE = cLevel.getBlockEntity(hitResult.getBlockPos());

        if (!(clickedBE instanceof SmartBlockEntity sbe) || !hasBehaviour(sbe))
            return false;

        return true;
    }
}
