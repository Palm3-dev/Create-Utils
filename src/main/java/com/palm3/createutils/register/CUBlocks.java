package com.palm3.createutils.register;

import com.palm3.createutils.content.blocks.adjustable_speed_controller.compact.AdjustableSpeedControllerCompactBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.palm3.createutils.CUMain.*;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class CUBlocks {

	@SuppressWarnings("removal")
	public static final BlockEntry<AdjustableSpeedControllerCompactBlock> ADJ_SPEED_CONTROLLER_COMPACT = CU_REGISTRATE
			.block("adjustable_speed_controller_compact", AdjustableSpeedControllerCompactBlock::new)
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_BROWN))
			.transform(CUStress.setNoImpact())
			.transform(axeOrPickaxe())
			.blockstate((c, p) -> {
				ResourceLocation baseModelLoc = ResourceLocation.fromNamespaceAndPath("create", "block/sequenced_gearshift/idle");
				String baseTextureName = "block/adjustable_speed_controller/compact/adjustable_speed_controller_compact_";
				ModelFile model0 = p.models().withExistingParent(c.getName() + "_0", baseModelLoc).texture("4", modRes(baseTextureName + "0"));
				ModelFile model1 = p.models().withExistingParent(c.getName() + "_1", baseModelLoc).texture("4", modRes(baseTextureName + "1"));
				ModelFile model2 = p.models().withExistingParent(c.getName() + "_2", baseModelLoc).texture("4", modRes(baseTextureName + "2"));
				ModelFile model3 = p.models().withExistingParent(c.getName() + "_3", baseModelLoc).texture("4", modRes(baseTextureName + "3"));
				ModelFile model4 = p.models().withExistingParent(c.getName() + "_4", baseModelLoc).texture("4", modRes(baseTextureName + "4"));
				ModelFile model5 = p.models().withExistingParent(c.getName() + "_5", baseModelLoc).texture("4", modRes(baseTextureName + "5"));

				p.getVariantBuilder(c.getEntry()).forAllStates(state -> {
					int yRot;
					int xRot;
					switch (state.getValue(BlockStateProperties.AXIS)) {
						case Y -> {yRot = 0; xRot = 90;}
						case X -> {yRot = 90; xRot = 0;}
						case Z -> {yRot = 0; xRot = 0;}
						case null, default -> throw new IllegalStateException("BlockState gen for '" + c.getName() + "' failed! Unexpected axis value.");
					}
					int power = state.getValue(AdjustableSpeedControllerCompactBlock.POWER);
					ModelFile model = switch (power) {
						case 0 -> model0;
						case 1, 2, 3 -> model1;
						case 4, 5, 6, 7 -> model2;
						case 8, 9, 10, 11 -> model3;
						case 12, 13, 14 -> model4;
						case 15 -> model5;
						default -> throw new IllegalStateException("Property 'AdjustableSpeedControllerBlockCompact.POWER' cannot be value " + power);
					};
					return ConfiguredModel.builder().rotationY(yRot).rotationX(xRot).modelFile(model).build();
				});
			})
			.item()
			.model((c, p) ->
					p.withExistingParent(c.getName(), ResourceLocation.fromNamespaceAndPath("create", "block/sequenced_gearshift/item"))
							.texture("4", modRes("block/adjustable_speed_controller/compact/adjustable_speed_controller_compact_0"))
			)
			.recipe((c, p) -> {
				ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, c.get())
						.pattern("bc ")
						.pattern("t  ")
						.define('b', AllBlocks.BRASS_CASING)
						.define('c', AllBlocks.COGWHEEL)
						.define('t', AllItems.ELECTRON_TUBE)
						.unlockedBy("has_electron_tube", has(AllItems.ELECTRON_TUBE))
						.save(p);
			})
			.build()
			.onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.create_utils.adjustable_speed_controller_compact"))
			.lang("Compact Adjustable Speed Controller")
			.register();

    public static void register() {}
}
