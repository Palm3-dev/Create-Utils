package com.palm3.createutils.register;

import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CUBlockEntities {
    public static final BlockEntityEntry<SmarterObserverBlockEntity> SMARTER_OBSERVER_BE = CU_REGISTRATE
            .blockEntity("smarter_observer_be", SmarterObserverBlockEntity::new)
            .validBlocks(CUBlocks.SMARTER_OBSERVER::get)
            .register();


    public static void register() {}
}
