package com.palm3.createutils.register;

import com.palm3.createutils.content.blocks.smarter_observer.SmarterObserverBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import static com.palm3.createutils.CUMain.CU_REGISTRATE;

public class CUBlocks {

    public static final BlockEntry<SmarterObserverBlock> SMARTER_OBSERVER = CU_REGISTRATE
            .block("smarter_observer", SmarterObserverBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .blockstate((c, p) -> p.models().withExistingParent(c.getName(), ResourceLocation.fromNamespaceAndPath("minecraft", "glass")))
            .simpleItem()
            .register();


    public static void register() {}
}
