package com.palm3.createutils.register;

import com.palm3.createutils.Helpers;
import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlock;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.palm3.createutils.CUMain.*;
import static com.palm3.createutils.Helpers.*;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class CUBlocks {

    public static final BlockEntry<SmarterObserverBlock> SMARTER_OBSERVER = CU_REGISTRATE
            .block("smarter_observer", SmarterObserverBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .blockstate((c, p) -> {
                ModelFile powered = p.models().getExistingFile(modRes("block/smarter_observer/observer_powered"));
                ModelFile unpowered = p.models().getExistingFile(modRes("block/smarter_observer/observer_unpowered"));

                p.getVariantBuilder(c.getEntry()).forAllStates(state -> {
                    ModelFile model = state.getValue(SmarterObserverBlock.POWERED) ? powered : unpowered;

                    Direction facing = state.getValue(SmarterObserverBlock.FACING);

                    int xRot = switch (facing) {
                        case DOWN -> 270;
                        case UP -> 90;
                        default -> 0;
                    };

                    int yRot = switch (facing) {
                        case EAST -> 90;
                        case SOUTH -> 180;
                        case WEST -> 270;
                        default -> 0;
                    };


                    return ConfiguredModel.builder().modelFile(model).rotationY(yRot).rotationX(xRot).build();
                });
            })
            .item()
            .model((c, p) -> p.blockItem(c::get, "/observer_unpowered"))
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, c.get())
                    .pattern(" s ")
                    .pattern(" e ")
                    .pattern(" o ")
                    .define('s', cPlates("brass"))
                    .define('e', AllItems.ELECTRON_TUBE)
                    .define('o', Items.OBSERVER)
                    .unlockedBy("has_electron_tube", has(AllItems.ELECTRON_TUBE))
                    .save(p)
            )
            .build()
            .register();


    public static void register() {}
}
