package com.palm3.createutils.foundation.mixins.scroll_value;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen")
public class ValueSettingsScreenMixin_OLD_TEMP {

    @Unique private static final int createutils$MILESTONES = 12;

    @Shadow private int maxLabelWidth;
    @Shadow private int valueBarWidth;
    @Shadow private ValueSettingsBoard board;
    @Shadow private int milestoneSize;
    @Shadow private boolean iconMode;

    /*@Inject(
            method = "init",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void init(CallbackInfo ci) {
        int maxValue = board.maxValue();
        maxLabelWidth = 0;
        int milestoneCount = maxValue / board.milestoneInterval() + 1;
        int scale = maxValue > 128 ? 1 : 2;

        for (Component component : board.rows())
            maxLabelWidth = Math.max(maxLabelWidth, font.width(component));
        if (iconMode)
            maxLabelWidth = -18;

        valueBarWidth = (maxValue + 1) * scale + 1 + milestoneCount * milestoneSize;
        int width = (maxLabelWidth + 14) + (valueBarWidth + 10);
        int height = (board.rows()
                .size() * 11);

        setWindowSize(width, height);
        super.init();

        Vec2 coordinateOfValue = getCoordinateOfValue(initialSettings.row(), initialSettings.value());
        setCursor(coordinateOfValue);
        ci.cancel();
    }*/

    @Inject(
            method = "init",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsScreen;valueBarWidth:I",
                    ordinal = 0,
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void init$modify_milestoneCount(CallbackInfo ci) {
        /*int maxValue = board.maxValue();
        int scale = maxValue > 128 ? 1 : 2;
        int milestoneCount = maxValue / board.milestoneInterval() + 1;
        milestoneCount = 9;
        int modifiedValueBarWidth = (maxValue + 1) * scale + 1 + milestoneCount * milestoneSize;
        LogUtils.getLogger().info("executed with width: {}", valueBarWidth);*/
        valueBarWidth = 366;  // add if > 256, maybe max value to fit mini mc screen
    }

    @ModifyVariable(
            method = "init",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "milestoneCount"
    )
    private int init$modify_milestoneCount(int value) {
        return createutils$MILESTONES;
    }

    @ModifyVariable(
            method = "renderWindow",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "milestoneCount"
    )
    private int renderWindow$modify_milestoneCount(int value) {
        return createutils$MILESTONES;
    }





    /*@Shadow private ValueSettingsBoard board;

    @ModifyVariable(
            method = "init",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "scale"
    )
    private int modifyScale(int value) {
        int maxValue = board.maxValue();
        return 2;
    }*/
}
