package com.palm3.createutils;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;

public class CUMain {
    public static final String MOD_ID = "createutils";
    public static final CreateRegistrate CU_REGISTRATE = CreateRegistrate.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public CUMain(IEventBus modEventBus) {

    }
}
