package com.palm3.createutils;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.config.CUCommonConfig;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import com.palm3.createutils.register.*;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItemRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Unique;

@Mod(CUMain.MOD_ID)
@EventBusSubscriber
public class CUMain {
    public static final String MOD_ID = "create_utils";
    public static final String CREATE_VERSION = "6.0.7-159";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate CU_REGISTRATE = CreateRegistrate
            .create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));

    public CUMain(IEventBus modEventBus, ModContainer modContainer) {
        CU_REGISTRATE.registerEventListeners(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CUCommonConfig.COMMON_CONFIG);

        //CUPackets.register();
        CUBlocks.register();
        CUBlockEntities.register();
        CUItems.register();
        CUTabs.CU_TABS.register(modEventBus);
        CULangs.addLangs();
    }

    public static ResourceLocation modRes(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(CUItems.SCREWDRIVER, ScrewdriverItem.DECORATOR);
    }

    public static boolean shouldDisableScrollValues() {
        if (!CUCommonConfig.ENABLE_OLD_LIKE_SPEED_SETTINGS.getAsBoolean())
            return false;

        if (CUCommonConfig.DISABLE_SCROLL_VALUES_MODE.get() == CUCommonConfig.DisableScrollValuesMode.ALWAYS)
            return true;

        Player player = Minecraft.getInstance().player;

        if (player == null)
            return false;

        ItemStack stackInHand = player.getItemInHand(player.getUsedItemHand());

        return CUCommonConfig.DISABLE_SCROLL_VALUES_MODE.get() == CUCommonConfig.DisableScrollValuesMode.WHEN_SCREWDRIVER && stackInHand.getItem() instanceof ScrewdriverItem;
    }
}
