package com.palm3.createutils.register;

import com.palm3.createutils.content.adjustable_speed_controller.compact.AdjustableSpeedControllerCompactBlock;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.palm3.createutils.CUMain.*;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class CUBlocks {

    /*public static final BlockEntry<SmarterObserverBlock> SMARTER_OBSERVER = CU_REGISTRATE
            .block("smarter_observer", SmarterObserverBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
            .blockstate((c, p) -> {
                ModelFile powered = p.models().getExistingFile(modRes("block/smarter_observer/observer_powered"));
                ModelFile unpowered = p.models().getExistingFile(modRes("block/smarter_observer/observer_unpowered"));

                p.getVariantBuilder(c.getEntry()).forAllStates(state -> {
                    Direction hFacing = state.getValue(SmarterObserverBlock.H_FACING);
                    AttachFace target = state.getValue(SmarterObserverBlock.TARGET);
                    ModelFile model = state.getValue(SmarterObserverBlock.POWERED) ? powered : unpowered;

                    int xRot = switch (target) {
                        case WALL -> 0;
                        case FLOOR -> 90;
                        case CEILING -> -90;
                    };

                    int yRot = switch (hFacing) {
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
            .register();*/

    //SPEED CONTROLLER FROM CREATE

    /*public static final BlockEntry<AdjustableSpeedControllerBlockFull> ADJ_SPEED_CONTROLLER_FULL =
		CU_REGISTRATE.block("rotation_speed_controller", AdjustableSpeedControllerBlockFull::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			//.transform(axeOrPickaxe())
			.tag(AllTags.AllBlockTags.SAFE_NBT.tag)
			.transform(CUStress.setNoImpact())
			.blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
			.item()
			.transform(customItemModel())
			.register();*/

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
			.build()
			.onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.create_utils.adjustable_speed_controller_compact"))
			.lang("Compact Adjustable Speed Controller")
			.register();

    public static void register() {}
}
