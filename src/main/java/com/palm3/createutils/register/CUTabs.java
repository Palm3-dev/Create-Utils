package com.palm3.createutils.register;

import com.palm3.createutils.CUMain;
import com.palm3.createutils.foundation.items.NonexistentItemsRegisterer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTabs {
    public static final DeferredRegister<CreativeModeTab> CU_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CUMain.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CU_TABS
            .register("main_creative_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_utils.main_creative_tab"))
                    .icon(CUItems.SCREWDRIVER::asStack)
                    .displayItems((params, output) -> {
                        output.accept(CUBlocks.ADJ_SPEED_CONTROLLER_COMPACT.asItem());
                        output.accept(CUItems.SCREWDRIVER);
                    })
                    .build()
            );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NONEXISTENT_ITEMS_TAB = CU_TABS
            .register("nonexistent_items_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_utils.nonexistent_items_tab"))
                    .icon(() -> new ItemStack(Items.BARRIER))
                    .displayItems((params, output) -> {
                        for (Item item : BuiltInRegistries.ITEM) {
                            if (item instanceof NonexistentItemsRegisterer.NonexistentBlockItem) {
                                output.accept(item);
                            }
                        }
                    })
                    .build()
            );
}
