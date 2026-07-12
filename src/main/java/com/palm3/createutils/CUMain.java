package com.palm3.createutils;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.foundation.block_items_addition.NonexistingBlockItemCreator;
import com.palm3.createutils.register.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CUMain.MOD_ID)
public class CUMain {
    public static final String MOD_ID = "create_utils";
    public static final String CREATE_VERSION = "6.0.7-159";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate CU_REGISTRATE = CreateRegistrate.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public CUMain(IEventBus modEventBus, ModContainer modContainer) {
        CU_REGISTRATE.registerEventListeners(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CUCommonConfig.COMMON_CONFIG);




        CUPackets.register();
        CUBlocks.register();
        CUBlockEntities.register();
        CUTabs.CU_TABS.register(modEventBus);
        CULangs.addLangs();
    }

    public static ResourceLocation modRes(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
