package com.palm3.createutils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class Helpers {

    private final String MOD_ID;
    public Helpers(String mod_id) {
        MOD_ID = mod_id;
    }



    public TagKey<Item> modTag(String tag) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, tag));
    }




    //========================= NEOFORGE ============================
    public static TagKey<Item> neoTag(String tag) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", tag));
    }

    public static TagKey<Item> cPlates(String material) {
        return neoTag("plates/" + material);
    }

    public static TagKey<Item> cNuggets(String material) {
        return neoTag("nuggets/" + material);
    }

    public static TagKey<Item> cIngots(String material) {
        return neoTag("ingots/" + material);
    }

    public static TagKey<Item> cRods(String material) {
        return neoTag("rods/" + material);
    }
}
