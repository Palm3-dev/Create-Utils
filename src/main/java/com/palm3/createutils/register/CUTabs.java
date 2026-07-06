package com.palm3.createutils.register;

import com.palm3.createutils.CUMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTabs {
    public static final DeferredRegister<CreativeModeTab> CU_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CUMain.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CU_TABS
            .register("main_creative_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.createutils.main_creative_tab"))
                    .icon(() -> new ItemStack(Items.IRON_BLOCK.asItem()))
                    .displayItems((params, output) -> {
                        CUBlocks.SMARTER_OBSERVER.asItem();
                        Items.STICK.asItem();
                    })
                    .build()
            );
}
