package com.palm3.createutils.register;

import com.palm3.createutils.content.adjustable_speed_controller.compact.AdjustableSpeedControllerCompactBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CUBlockEntities {
    /*public static final BlockEntityEntry<SmarterObserverBlockEntity> SMARTER_OBSERVER_BE = CU_REGISTRATE
            .blockEntity("smarter_observer_be", SmarterObserverBlockEntity::new)
            .validBlocks(CUBlocks.SMARTER_OBSERVER::get)
            .register();*/

    public static final BlockEntityEntry<AdjustableSpeedControllerCompactBlockEntity> ADJ_SPEED_CONTROLLER_BE_COMPACT = CU_REGISTRATE
            .blockEntity("adj_speed_controller_compact_be", AdjustableSpeedControllerCompactBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlock(CUBlocks.ADJ_SPEED_CONTROLLER_COMPACT::get)
            .renderer(() -> SplitShaftRenderer::new)
            .register();


    public static void register() {}
}
