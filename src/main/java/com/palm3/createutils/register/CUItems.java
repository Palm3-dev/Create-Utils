package com.palm3.createutils.register;

import com.palm3.createutils.Helpers;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;
import static com.palm3.createutils.CUMain.modRes;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class CUItems {
    public static final ItemEntry<ScrewdriverItem> SCREWDRIVER = CU_REGISTRATE
            .item("screwdriver", ScrewdriverItem::new)
            .properties(p -> p.stacksTo(1))
            .model((c, p) -> p.getExistingFile(modRes("item/" + c.getName())))
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, c.get())
                    .pattern("i  ")
                    .pattern("w  ")
                    .define('i', Helpers.cRods("iron"))
                    .define('w', Items.RED_WOOL)
                    .unlockedBy("has_iron_rod", has(Helpers.cRods("iron")))
                    .save(p)
            )
            .onRegisterAfter(Registries.ITEM, i -> ItemDescription.useKey(i, "item.create_utils.screwdriver"))
            .register();

    public static void register() {}
}
