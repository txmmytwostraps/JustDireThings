package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.common.blocks.BlockBreakerT1;
import com.direwolf20.justdirethings.common.blocks.gooblocks.GooBlock_Base;
import com.direwolf20.justdirethings.common.blocks.gooblocks.GooPatternBlock;
import com.direwolf20.justdirethings.common.blocks.resources.TimeCrystalBuddingBlock;
import com.direwolf20.justdirethings.common.blocks.resources.TimeCrystalCluster;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

import static com.direwolf20.justdirethings.JustDireThings.MODID;

public class JustDireBlockStates extends BlockStateProvider {
	public JustDireBlockStates(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		gooBlocks(Registration.GooBlock_Tier1);
		gooBlocks(Registration.GooBlock_Tier2);
		gooBlocks(Registration.GooBlock_Tier3);
		gooBlocks(Registration.GooBlock_Tier4);
		simpleBlock(Registration.FerricoreBlock.get(), models().cubeAll(Registration.FerricoreBlock_ITEM.getId().getPath(), blockTexture(Registration.FerricoreBlock.get())));
		simpleBlock(Registration.BlazeGoldBlock.get(), models().cubeAll(Registration.BlazeGoldBlock_ITEM.getId().getPath(), blockTexture(Registration.BlazeGoldBlock.get())));
		simpleBlock(Registration.CelestigemBlock.get(), models().cubeAll(Registration.CelestigemBlock_ITEM.getId().getPath(), blockTexture(Registration.CelestigemBlock.get())));
		simpleBlock(Registration.EclipseAlloyBlock.get(), models().cubeAll(Registration.EclipseAlloyBlock_ITEM.getId().getPath(), blockTexture(Registration.EclipseAlloyBlock.get())));
		simpleBlock(Registration.CoalBlock_T1.get(), models().cubeAll(Registration.CoalBlock_T1.getId().getPath(), blockTexture(Registration.CoalBlock_T1.get())));
		simpleBlock(Registration.CharcoalBlock.get(), models().cubeAll(Registration.CharcoalBlock.getId().getPath(), blockTexture(Registration.CharcoalBlock.get())));
		simpleBlock(Registration.CoalBlock_T2.get(), models().cubeAll(Registration.CoalBlock_T2.getId().getPath(), blockTexture(Registration.CoalBlock_T2.get())));
		simpleBlock(Registration.CoalBlock_T3.get(), models().cubeAll(Registration.CoalBlock_T3.getId().getPath(), blockTexture(Registration.CoalBlock_T3.get())));
		simpleBlock(Registration.CoalBlock_T4.get(), models().cubeAll(Registration.CoalBlock_T4.getId().getPath(), blockTexture(Registration.CoalBlock_T4.get())));
		simpleBlock(Registration.PlayerAccessor.get(), models().cubeAll(Registration.PlayerAccessor.getId().getPath(), blockTexture(Registration.PlayerAccessor.get())));
		simpleBlock(Registration.EclipseGateBlock.get(), models().cubeAll(Registration.EclipseGateBlock.getId().getPath(), blockTexture(Registration.EclipseGateBlock.get())));
		simpleBlock(Registration.TimeCrystalBlock.get(), models().cubeAll(Registration.TimeCrystalBlock.getId().getPath(), blockTexture(Registration.TimeCrystalBlock.get())));
		simpleBlock(Registration.InventoryHolder.get(), models().cubeAll(Registration.InventoryHolder.getId().getPath(), blockTexture(Registration.InventoryHolder.get())));
		direSmithingTable();
		timeCrystalCluster(Registration.TimeCrystalCluster_Small);
		timeCrystalCluster(Registration.TimeCrystalCluster_Medium);
		timeCrystalCluster(Registration.TimeCrystalCluster_Large);
		timeCrystalCluster(Registration.TimeCrystalCluster);
		soilBlocks(Registration.GooSoil_Tier1);
		soilBlocks(Registration.GooSoil_Tier2);
		soilBlocks(Registration.GooSoil_Tier3);
		soilBlocks(Registration.GooSoil_Tier4);
		oreBlocks(Registration.RawBlazegoldOre);
		oreBlocks(Registration.RawCelestigemOre);
		oreBlocks(Registration.RawEclipseAlloyOre);
		oreBlocks(Registration.RawFerricoreOre);
		oreBlocks(Registration.RawCoal_T1);
		oreBlocks(Registration.RawCoal_T2);
		oreBlocks(Registration.RawCoal_T3);
		oreBlocks(Registration.RawCoal_T4);
		patternBlock();
		sidedBlocks();
		fluidBlocks();

		getVariantBuilder(Registration.TimeCrystalBuddingBlock.get()).forAllStates(s -> {
			ModelFile model;
			int stage = s.getValue(TimeCrystalBuddingBlock.STAGE);
			if (stage == 0) {
				model = models().cubeAll("time_crystal_budding_block_state_0", modLoc("block/time_crystal_budding_block_state_0"));
			} else if (stage == 1) {
				model = models().cubeAll("time_crystal_budding_block_state_1", modLoc("block/time_crystal_budding_block_state_1"));
			} else if (stage == 2) {
				model = models().cubeAll("time_crystal_budding_block_state_2", modLoc("block/time_crystal_budding_block_state_2"));
			} else {
				model = models().cubeAll("time_crystal_budding_block", modLoc("block/time_crystal_budding_block"));
			}

			return ConfiguredModel.builder()
					.modelFile(model)
					.build();
		});
	}

	// Laid out like the vanilla smithing table
	private void direSmithingTable() {
		String path = Registration.DireSmithingTable.getId().getPath();
		simpleBlock(Registration.DireSmithingTable.get(), models().cube(path,
				modLoc("block/" + path + "_bottom"),
				blockTexture(Registration.FerricoreBlock.get()),
				modLoc("block/" + path + "_front"),
				modLoc("block/" + path + "_front"),
				modLoc("block/" + path + "_side"),
				modLoc("block/" + path + "_side")
		).texture("particle", modLoc("block/" + path + "_front")));
	}

	private void timeCrystalCluster(RegistryObject<? extends TimeCrystalCluster> block) {
		var texturePath = block.getId().getPath();
		var texture = modLoc("block/" + texturePath);
		var model = models().cross(texturePath, texture);
		directionalBlock(block.get(), model);
	}

	private void gooBlocks(RegistryObject<? extends GooBlock_Base> block) {
		getVariantBuilder(block.get()).forAllStates(state -> {
			boolean alive = state.getValue(GooBlock_Base.ALIVE);
			String texturePath = alive ? block.getId().getPath() : block.getId().getPath() + "_dead";
			return ConfiguredModel.builder()
					.modelFile(models().cubeAll(texturePath, modLoc("block/" + texturePath)))
					.build();
		});
	}

	private void fluidBlocks() {
		for (var fluidBlock : Registration.FLUID_BLOCKS.getEntries()) {
			simpleBlock(fluidBlock.get(), models().getBuilder(fluidBlock.getId().getPath()).texture("particle", ResourceLocation.fromNamespaceAndPath(MODID, ModelProvider.BLOCK_FOLDER + "/" + "fluid_source")));
		}
	}

	private void sidedBlocks() {
		for (var sidedBlock : Registration.SIDEDBLOCKS.getEntries()) {
			if (sidedBlock.equals(Registration.BlockBreakerT1)) {
				getVariantBuilder(Registration.BlockBreakerT1.get()).forAllStates(s -> {
					ModelFile model;
					boolean active = s.getValue(BlockBreakerT1.ACTIVE);
					Direction dir = s.getValue(BlockStateProperties.FACING);
					if (active) { // Active
						model = models().orientableWithBottom(
								Objects.requireNonNull(sidedBlock.getId()).getPath() + "_active",
								modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_bottom"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_top_active")
						);
					} else {
						model = models().orientableWithBottom(
								Objects.requireNonNull(sidedBlock.getId()).getPath(),
								modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_bottom"),
								modLoc("block/" + sidedBlock.getId().getPath() + "_top")
						);
					}
					return ConfiguredModel.builder()
							.modelFile(model)
							.rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
							.rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
							.build();
				});
			} else {
				ModelFile model = models().orientableWithBottom(
						Objects.requireNonNull(sidedBlock.getId()).getPath(),
						modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
						modLoc("block/" + sidedBlock.getId().getPath() + "_side"),
						modLoc("block/" + sidedBlock.getId().getPath() + "_bottom"),
						modLoc("block/" + sidedBlock.getId().getPath() + "_top")
				);

				directionalBlock(sidedBlock.get(), model);
			}
		}
	}

	private void patternBlock() {
		getVariantBuilder(Registration.GooPatternBlock.get()).forAllStates(s -> {
			int stage = s.getValue(GooPatternBlock.GOOSTAGE);
			ModelFile model;
			if (stage == 11) {
				model = models().orientableWithBottom(
						Objects.requireNonNull(Registration.GooPatternBlock.getId()).getPath() + "11",
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full")
				);
			} else if (stage == 10) {
				model = models().orientableWithBottom(
						Objects.requireNonNull(Registration.GooPatternBlock.getId()).getPath() + "10",
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full")
				);
			} else if (stage == 9) {
				model = models().orientableWithBottom(
						Objects.requireNonNull(Registration.GooPatternBlock.getId()).getPath() + "9",
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goopatterblock_top")
				);
			} else {
				model = models().orientableWithBottom(
						Objects.requireNonNull(Registration.GooPatternBlock.getId()).getPath() + stage,
						modLoc("block/goopatterns/goorender_side" + stage),
						modLoc("block/goopatterns/goorender_side" + stage),
						modLoc("block/goopatterns/goorender_full"),
						modLoc("block/goopatterns/goorender_blank")
				);
			}
			return ConfiguredModel.builder()
					.modelFile(model).build();
		});
	}

	private void soilBlocks(RegistryObject<? extends Block> soil) {
		getVariantBuilder(soil.get()).forAllStates(s -> {
			int Moisture = s.getValue(BlockStateProperties.MOISTURE);
			return ConfiguredModel.builder()
					.modelFile(models()
							.withExistingParent(soil.getId().getPath() + (Moisture == 7 ? "_moist" : ""),
									ResourceLocation.parse("minecraft:block/template_farmland"))
							.texture("dirt",
									modLoc("block/goosoilside_tier"
											+ soil.getId().toString().replace(MODID + ":goosoil_tier", "")))
							.texture("top",
									modLoc("block/goofarmland_tier"
											+ soil.getId().toString().replace(MODID + ":goosoil_tier", "")
											+ (Moisture == 7 ? "_moist" : ""))))
					.build();
		});
	}

	private void oreBlocks(RegistryObject<? extends Block> block) {
		directionalBlock(block.get(), models()
				.withExistingParent(block.getId().getPath(), MODID + ":block/raw_ore_template")
				.texture("palette", block.getId().toString().replace(":", ":block/")));
	}
}
