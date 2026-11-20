package com.palm3.createutils.foundation.items;

import com.mojang.logging.LogUtils;
import com.palm3.createutils.CUMain;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

/*
    Registers items for any block that doesn't have any in order to be used as filters.
 */

@EventBusSubscriber(modid = CUMain.MOD_ID)
public class NonexistentItemsRegisterer {

    private static final List<ResourceLocation> BLOCKS_WITH_REGISTERED_ITEMS = new ArrayList<>();
    private static final List<ResourceLocation> BLOCKS_EXCLUSIONS = List.of(
            getId(Blocks.AIR),
            getId(Blocks.CAVE_AIR),
            getId(Blocks.VOID_AIR),
            getId(Blocks.WATER),
            getId(Blocks.LAVA),
            getId(Blocks.BUBBLE_COLUMN),
            getId(Blocks.MOVING_PISTON),
            ResourceLocation.parse("create:chocolate"),
            ResourceLocation.parse("create:honey"),
            ResourceLocation.parse("create:crushing_wheel_controller"),
            ResourceLocation.parse("create:fake_track")
    );

    private static ResourceLocation getId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    // Registers items
    @SubscribeEvent
    public static void onRegisterItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(BuiltInRegistries.ITEM.key()))
            return;

        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            if (BLOCKS_EXCLUSIONS.contains(blockId))
                continue;

            if (block.asItem() == Items.AIR) {
                Item.Properties props = new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.UNCOMMON);
                NonexistentBlockItem blockItem = new NonexistentBlockItem(block, props);
                LogUtils.getLogger().info("Registering item for block '{}' with same id", blockId);
                BLOCKS_WITH_REGISTERED_ITEMS.add(blockId);
                event.register(BuiltInRegistries.ITEM.key(), blockId, () -> blockItem);
            }
        }
    }

    // Adds models for previously registered items
    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.ModifyBakingResult event) {
        for (ResourceLocation blockId : BLOCKS_WITH_REGISTERED_ITEMS) {
            BlockState state = BuiltInRegistries.BLOCK.get(blockId).defaultBlockState();
            ModelResourceLocation blockModelLocation = BlockModelShaper.stateToModelLocation(state);

            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(BuiltInRegistries.ITEM.get(blockId));
            ModelResourceLocation itemModelLocation = new ModelResourceLocation(itemId, ModelResourceLocation.INVENTORY_VARIANT);

            BakedModel bakedModel = event.getModels().get(blockModelLocation);
            event.getModels().put(itemModelLocation, bakedModel);
        }
    }

    // Class created to distinguish block items that have been registered here.
    public static class NonexistentBlockItem extends BlockItem {
        public NonexistentBlockItem(Block block, Properties properties) {
            super(block, properties);
        }
    }
}
