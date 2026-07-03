package com.palm3.createutils;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.config.CUCommonConfig;
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
    public static final String MOD_ID = "createutils";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String CREATE_VERSION = "6.0.7-159";
    public static final CreateRegistrate CU_REGISTRATE = CreateRegistrate.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public CUMain(IEventBus modEventBus, ModContainer modContainer) {
        CU_REGISTRATE.registerEventListeners(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CUCommonConfig.COMMON_CONFIG);

        CUBlocks.register();
        CUBlockEntities.register();
        CUTabs.CU_TABS.register(modEventBus);
        CULangs.addLangs();
    }

    public static ResourceLocation modRes(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void logIfExtensive(String msg, int logLevel_012orHigher) {
        if (CUCommonConfig.LOG_ALL.getAsBoolean())
            switch (logLevel_012orHigher) {
                case 0: LOGGER.info(msg);
                case 1: LOGGER.warn(msg);
                case 2: LOGGER.error(msg);
                default:
                    LOGGER.warn("The next message from 'co.pa.cr.CUMain' is logged 'info' because logIfExtensive() received logLevel > 2");
                    LOGGER.info(msg);
            }
    }

    /// Logs if enabled in config, put 'empty' for empty line marked by |.
    public static void l(String msg) {
        if (CUCommonConfig.LOG_ALL.getAsBoolean()) {
            if (msg.equals("empty")) {
                CUMain.LOGGER.info("|");
                CUMain.LOGGER.info("|");
            } else CUMain.LOGGER.info(msg);
        }
    }
}
