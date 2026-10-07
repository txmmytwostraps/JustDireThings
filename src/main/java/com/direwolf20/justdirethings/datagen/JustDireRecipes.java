package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.datagen.recipes.AbilityRecipeBuilder;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipeBuilder;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipeBuilder;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipeTagBuilder;
import com.direwolf20.justdirethings.datagen.recipes.PaxelRecipeBuilder;
import com.direwolf20.justdirethings.datagen.recipes.SculkRecipesCondition;
import com.direwolf20.justdirethings.datagen.recipes.TierUpgradeRecipeBuilder;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import java.util.EnumSet;
import java.util.function.Consumer;

public class JustDireRecipes extends RecipeProvider {

	public JustDireRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		// Goo Blocks
		ShapedRecipeBuilder.shaped(Registration.GooBlock_Tier1.get())
				.pattern("csc")
				.pattern("fdf")
				.pattern("csc")
				.define('d', Items.DIRT)
				.define('c', Items.CLAY_BALL)
				.define('s', Items.SUGAR)
				.define('f', Items.ROTTEN_FLESH)
				.group("justdirethings")
				.unlockedBy("has_coal", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COAL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.GooBlock_Tier2.get())
				.pattern("brb")
				.pattern("wgw")
				.pattern("brb")
				.define('g', Registration.GooBlock_Tier1_ITEM.get())
				.define('b', Items.BLAZE_POWDER)
				.define('r', Tags.Items.DUSTS_REDSTONE)
				.define('w', Items.NETHER_WART)
				.group("justdirethings")
				.unlockedBy("has_goo_block1", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier1_ITEM.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.GooBlock_Tier3.get())
				.pattern("brb")
				.pattern("wgw")
				.pattern("brb")
				.define('g', Registration.GooBlock_Tier2_ITEM.get())
				.define('b', Items.ENDER_PEARL)
				.define('r', Items.END_STONE)
				.define('w', Items.DRAGON_BREATH)
				.group("justdirethings")
				.unlockedBy("has_goo_block2", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier2_ITEM.get()))
				.save(consumer);
		ConditionalRecipe.builder()
				.addCondition(SculkRecipesCondition.INSTANCE)
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.GooBlock_Tier4.get())
						.pattern("brb")
						.pattern("wgw")
						.pattern("brb")
						.define('g', Registration.GooBlock_Tier3_ITEM.get())
						.define('b', JustDireItemTags.SCULK)
						.define('r', JustDireItemTags.SCULK_SHRIEKER)
						.define('w', JustDireItemTags.ECHO_SHARD)
						.group("justdirethings")
						.unlockedBy("has_goo_block3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
						::save)
				.addCondition(new NotCondition(SculkRecipesCondition.INSTANCE))
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.GooBlock_Tier4.get())
						.pattern("brb")
						.pattern("wgw")
						.pattern("brb")
						.define('g', Registration.GooBlock_Tier3_ITEM.get())
						.define('b', Items.OBSIDIAN)
						.define('r', Items.DRAGON_BREATH)
						.define('w', Items.POPPED_CHORUS_FRUIT)
						.group("justdirethings")
						.unlockedBy("has_goo_block3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
						::save)
				.generateAdvancement()
				.build(consumer, Registration.GooBlock_Tier4.getId());

		// Machines
		ShapedRecipeBuilder.shaped(Registration.ItemCollector.get())
				.pattern(" d ")
				.pattern("heh")
				.pattern("fff")
				.define('e', Items.ENDER_PEARL)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.DIAMOND)
				.define('h', Items.HOPPER)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EnergyTransmitter.get())
				.pattern(" d ")
				.pattern("heh")
				.pattern("fff")
				.define('e', Items.ENDER_PEARL)
				.define('f', Registration.BlazegoldIngot.get())
				.define('d', Registration.Celestigem.get())
				.define('h', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.ExperienceHolder.get())
				.pattern(" d ")
				.pattern("heh")
				.pattern("fff")
				.define('e', Items.ENDER_PEARL)
				.define('f', Registration.BlazegoldIngot.get())
				.define('d', Items.EMERALD)
				.define('h', Items.BOOK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockBreakerT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.OBSERVER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.DIAMOND)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockBreakerT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.BlockBreakerT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockPlacerT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DISPENSER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.DIAMOND)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockPlacerT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.BlockPlacerT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.ClickerT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DISPENSER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.ENDER_EYE)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.ClickerT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.ClickerT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.SensorT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.OBSERVER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.ENDER_EYE)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.SensorT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.SensorT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.DropperT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DROPPER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.REDSTONE)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.DropperT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.DropperT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.GeneratorT1.get())
				.pattern("iri")
				.pattern("cfc")
				.pattern("iri")
				.define('i', Registration.FerricoreIngot.get())
				.define('c', Items.COAL)
				.define('r', Items.REDSTONE)
				.define('f', Items.BLAST_FURNACE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.GeneratorFluidT1.get())
				.pattern("grg")
				.pattern("bfb")
				.pattern("grg")
				.define('g', Registration.BlazegoldIngot.get())
				.define('b', Items.BUCKET)
				.define('r', Items.REDSTONE)
				.define('f', Items.BLAST_FURNACE)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockSwapperT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.OBSERVER)
				.define('f', Registration.BlazegoldIngot.get())
				.define('d', Items.ENDER_EYE)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlockSwapperT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.BlockSwapperT1_ITEM.get())
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PlayerAccessor.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DISPENSER)
				.define('f', Registration.BlazegoldIngot.get())
				.define('d', Items.ENDER_EYE)
				.define('l', Items.ENDER_PEARL)
				.define('r', Items.LAPIS_LAZULI)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.InventoryHolder.get())
				.pattern(" f ")
				.pattern("fof")
				.pattern(" f ")
				.define('o', Items.ARMOR_STAND)
				.define('f', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FluidPlacerT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DROPPER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.BUCKET)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FluidPlacerT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.FluidPlacerT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FluidCollectorT1.get())
				.pattern("fdf")
				.pattern("lol")
				.pattern("frf")
				.define('o', Items.DISPENSER)
				.define('f', Registration.FerricoreIngot.get())
				.define('d', Items.BUCKET)
				.define('l', Items.LAPIS_LAZULI)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FluidCollectorT2.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.FluidCollectorT1_ITEM.get())
				.define('f', Registration.Celestigem.get())
				.define('d', Items.ENDER_EYE)
				.define('r', Items.REDSTONE)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.ParadoxMachine.get())
				.pattern("fdf")
				.pattern("dod")
				.pattern("frf")
				.define('o', Registration.TimeCrystalBlock.get())
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('d', Registration.TimeCrystal.get())
				.define('r', Registration.TIME_FLUID_BUCKET.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.DireSmithingTable.get())
				.pattern("fff")
				.pattern("fof")
				.pattern("fff")
				.define('o', Items.SMITHING_TABLE)
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);

		// Resource
		ShapelessRecipeBuilder.shapeless(Registration.PolymorphicCatalyst.get(), 4)
				.requires(Registration.BlazegoldIngot.get())
				.requires(Items.NETHER_WART)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PortalFluidCatalyst.get())
				.pattern(" c ")
				.pattern("clc")
				.pattern(" c ")
				.define('c', Items.CHORUS_FRUIT)
				.define('l', Items.LAPIS_LAZULI)
				.group("justdirethings")
				.unlockedBy("has_chorus_fruit", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CHORUS_FRUIT))
				.save(consumer);

		// Items
		ShapedRecipeBuilder.shaped(Registration.FerricoreWrench.get())
				.pattern(" i ")
				.pattern(" ii")
				.pattern("i  ")
				.define('i', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.TotemOfDeathRecall.get())
				.pattern("bdb")
				.pattern("lel")
				.pattern("bdb")
				.define('b', Registration.BlazegoldIngot.get())
				.define('e', Items.ENDER_PEARL)
				.define('d', Items.DIAMOND)
				.define('l', Items.LAPIS_LAZULI)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.Fuel_Canister.get())
				.pattern(" i ")
				.pattern("ici")
				.pattern(" i ")
				.define('i', Registration.FerricoreIngot.get())
				.define('c', Items.COAL)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FluidCanister.get())
				.pattern(" i ")
				.pattern("igi")
				.pattern(" i ")
				.define('i', Registration.FerricoreIngot.get())
				.define('g', Items.GLASS)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PotionCanister.get())
				.pattern(" i ")
				.pattern("igi")
				.pattern(" i ")
				.define('i', Registration.FerricoreIngot.get())
				.define('g', Items.GLASS_BOTTLE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.Pocket_Generator.get())
				.pattern("iri")
				.pattern("cfc")
				.pattern("iri")
				.define('i', Registration.FerricoreIngot.get())
				.define('c', Items.COAL)
				.define('r', Items.REDSTONE_BLOCK)
				.define('f', Items.FURNACE)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazejetWand.get())
				.pattern("  b")
				.pattern(" i ")
				.pattern("i  ")
				.define('i', Registration.FerricoreIngot.get())
				.define('b', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.VoidshiftWand.get())
				.pattern("  c")
				.pattern(" w ")
				.pattern("b  ")
				.define('b', Registration.BlazegoldIngot.get())
				.define('w', Registration.BlazejetWand.get())
				.define('c', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EclipsegateWand.get())
				.pattern("  e")
				.pattern(" w ")
				.pattern("c  ")
				.define('e', Registration.EclipseAlloyIngot.get())
				.define('w', Registration.VoidshiftWand.get())
				.define('c', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PolymorphicWand.get())
				.pattern("  b")
				.pattern(" r ")
				.pattern("i  ")
				.define('i', Registration.BlazegoldIngot.get())
				.define('r', Registration.PolymorphicCatalyst.get())
				.define('b', Registration.FluidCanister.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PolymorphicWandV2.get())
				.pattern("  b")
				.pattern(" r ")
				.pattern("i  ")
				.define('i', Registration.Celestigem.get())
				.define('r', Registration.PolymorphicWand.get())
				.define('b', Registration.EclipseAlloyIngot.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.TimeWand.get())
				.pattern(" bt")
				.pattern(" ib")
				.pattern("i  ")
				.define('i', Registration.FerricoreIngot.get())
				.define('b', Registration.BlazegoldIngot.get())
				.define('t', Registration.TimeCrystal.get())
				.group("justdirethings")
				.unlockedBy("has_time_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TimeCrystal.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.CreatureCatcher.get())
				.pattern(" b ")
				.pattern("beb")
				.pattern(" b ")
				.define('b', Registration.BlazegoldIngot.get())
				.define('e', Items.ENDER_PEARL)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.MachineSettingsCopier.get())
				.pattern("  c")
				.pattern(" p ")
				.pattern("f  ")
				.define('f', Registration.FerricoreIngot.get())
				.define('p', Items.PAPER)
				.define('c', Registration.Coal_T1.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PortalGun.get())
				.pattern(" bb")
				.pattern("beb")
				.pattern("b  ")
				.define('e', Items.ENDER_EYE)
				.define('b', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.PortalGunV2.get())
				.pattern(" bc")
				.pattern("beb")
				.pattern("bf ")
				.define('e', Registration.PortalFluidCatalyst.get())
				.define('b', Registration.BlazegoldIngot.get())
				.define('c', Registration.Celestigem.get())
				.define('f', Registration.FluidCanister.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);

		// Upgrades
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_BASE.get())
				.pattern(" f ")
				.pattern("f f")
				.pattern(" f ")
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.TEMPLATE_FERRICORE.get())
				.pattern("f f")
				.pattern("fbf")
				.pattern(" f ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Registration.FerricoreIngot.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(Registration.TEMPLATE_FERRICORE.get(), 2)
				.requires(Registration.TEMPLATE_FERRICORE.get())
				.requires(Registration.FerricoreIngot.get(), 2)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer, Registration.TEMPLATE_FERRICORE.getId() + "-duplicate");
		ShapedRecipeBuilder.shaped(Registration.TEMPLATE_BLAZEGOLD.get())
				.pattern("f f")
				.pattern("fbf")
				.pattern(" f ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Registration.BlazegoldIngot.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(Registration.TEMPLATE_BLAZEGOLD.get(), 2)
				.requires(Registration.TEMPLATE_BLAZEGOLD.get())
				.requires(Registration.BlazegoldIngot.get(), 2)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer, Registration.TEMPLATE_BLAZEGOLD.getId() + "-duplicate");
		ShapedRecipeBuilder.shaped(Registration.TEMPLATE_CELESTIGEM.get())
				.pattern("f f")
				.pattern("fbf")
				.pattern(" f ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Registration.Celestigem.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(Registration.TEMPLATE_CELESTIGEM.get(), 2)
				.requires(Registration.TEMPLATE_CELESTIGEM.get())
				.requires(Registration.Celestigem.get(), 2)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer, Registration.TEMPLATE_CELESTIGEM.getId() + "-duplicate");
		ShapedRecipeBuilder.shaped(Registration.TEMPLATE_ECLIPSEALLOY.get())
				.pattern("f f")
				.pattern("fbf")
				.pattern(" f ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Registration.EclipseAlloyIngot.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(Registration.TEMPLATE_ECLIPSEALLOY.get(), 2)
				.requires(Registration.TEMPLATE_ECLIPSEALLOY.get())
				.requires(Registration.EclipseAlloyIngot.get(), 2)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer, Registration.TEMPLATE_ECLIPSEALLOY.getId() + "-duplicate");
		ShapelessRecipeBuilder.shapeless(Registration.UPGRADE_ELYTRA.get())
				.requires(Registration.UPGRADE_BASE.get())
				.requires(Items.ELYTRA)
				.group("justdirethings")
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_DEATHPROTECTION.get())
				.pattern("tet")
				.pattern("fbf")
				.pattern("tet")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.TOTEM_OF_UNDYING)
				.define('e', Items.EMERALD)
				.define('t', Registration.TimeCrystal.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_DEBUFFREMOVER.get())
				.pattern("mem")
				.pattern("fbf")
				.pattern("mem")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.GOLDEN_APPLE)
				.define('m', Items.MILK_BUCKET)
				.define('e', Items.HONEY_BOTTLE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_MOBSCANNER.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.AMETHYST_SHARD)
				.define('e', Items.SPIDER_EYE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_OREMINER.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.IRON_PICKAXE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_ORESCANNER.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.AMETHYST_SHARD)
				.define('e', Items.REDSTONE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_LAWNMOWER.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.GRASS)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_SKYSWEEPER.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.GRAVEL)
				.define('e', Items.SAND)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_LEAFBREAKER.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', ItemTags.LEAVES)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_RUNSPEED.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.SUGAR)
				.define('e', Items.POTATO)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_WALKSPEED.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.SUGAR)
				.define('e', Items.CARROT)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_STEPHEIGHT.get())
				.pattern(" f ")
				.pattern("fbf")
				.pattern(" f ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.LADDER)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_JUMPBOOST.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.PISTON)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_MINDFOG.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.FERMENTED_SPIDER_EYE)
				.define('e', Items.LAPIS_LAZULI)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_INVULNERABILITY.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.DIAMOND_CHESTPLATE)
				.define('e', Items.SHIELD)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_TREEFELLER.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.DIAMOND_AXE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_SMELTER.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.BLAST_FURNACE)
				.define('e', Items.BLAZE_ROD)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_SMOKER.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.SMOKER)
				.define('e', Items.BLAZE_ROD)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_HAMMER.get())
				.pattern("   ")
				.pattern("fbf")
				.pattern("   ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.DIAMOND_PICKAXE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_CAUTERIZEWOUNDS.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.MAGMA_CREAM)
				.define('e', Items.GOLDEN_APPLE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_SWIMSPEED.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.SUGAR)
				.define('e', Items.KELP)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_WATERBREATHING.get())
				.pattern("fep")
				.pattern("gbg")
				.pattern("pef")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Registration.BlazegoldIngot.get())
				.define('f', Items.PRISMARINE_SHARD)
				.define('p', Items.PUFFERFISH)
				.define('e', Items.KELP)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_GROUNDSTOMP.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.SLIME_BLOCK)
				.define('e', Items.PISTON)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_EXTINGUISH.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.WATER_BUCKET)
				.define('e', Items.BLAZE_ROD)
				.define('a', Items.MAGMA_CREAM)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_STUPEFY.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.FERMENTED_SPIDER_EYE)
				.define('e', Items.POISONOUS_POTATO)
				.define('a', Items.ENDER_EYE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_DROPTELEPORT.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.AMETHYST_SHARD)
				.define('e', Items.ENDER_CHEST)
				.define('a', Items.ENDER_PEARL)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_NEGATEFALLDAMAGE.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', ItemTags.WOOL)
				.define('e', Items.LEATHER)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_NIGHTVISION.get())
				.pattern(" e ")
				.pattern("fbf")
				.pattern(" e ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.GOLDEN_CARROT)
				.define('e', Items.ENDER_EYE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_DECOY.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.ARMOR_STAND)
				.define('e', Items.WITHER_SKELETON_SKULL)
				.define('a', Items.PHANTOM_MEMBRANE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ConditionalRecipe.builder()
				.addCondition(SculkRecipesCondition.INSTANCE)
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_OREXRAY.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', JustDireItemTags.SCULK_SHRIEKER)
						.define('e', Items.SCULK_SENSOR)
						.define('a', Items.ENDER_EYE)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.addCondition(new NotCondition(SculkRecipesCondition.INSTANCE))
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_OREXRAY.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', Items.DRAGON_BREATH)
						.define('e', Items.OBSERVER)
						.define('a', Items.ENDER_EYE)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.generateAdvancement()
				.build(consumer, Registration.UPGRADE_OREXRAY.getId());
		ConditionalRecipe.builder()
				.addCondition(SculkRecipesCondition.INSTANCE)
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_GLOWING.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', JustDireItemTags.SCULK_SHRIEKER)
						.define('e', Items.SCULK_SENSOR)
						.define('a', Items.END_ROD)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.addCondition(new NotCondition(SculkRecipesCondition.INSTANCE))
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_GLOWING.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', Items.DRAGON_BREATH)
						.define('e', Items.OBSERVER)
						.define('a', Items.END_ROD)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.generateAdvancement()
				.build(consumer, Registration.UPGRADE_GLOWING.getId());
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_INSTABREAK.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.NETHERITE_PICKAXE)
				.define('e', Items.NETHER_STAR)
				.define('a', Registration.TimeCrystal.get())
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_EARTHQUAKE.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.STICKY_PISTON)
				.define('e', Items.FERMENTED_SPIDER_EYE)
				.define('a', Items.SOUL_SAND)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ConditionalRecipe.builder()
				.addCondition(SculkRecipesCondition.INSTANCE)
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_NOAI.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', Registration.TimeCrystal.get())
						.define('e', Items.END_ROD)
						.define('a', Items.SCULK_SENSOR)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.addCondition(new NotCondition(SculkRecipesCondition.INSTANCE))
				.addRecipe(ShapedRecipeBuilder.shaped(Registration.UPGRADE_NOAI.get())
						.pattern("aea")
						.pattern("fbf")
						.pattern("aea")
						.define('b', Registration.UPGRADE_BASE.get())
						.define('f', Registration.TimeCrystal.get())
						.define('e', Items.END_ROD)
						.define('a', Items.OBSERVER)
						.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
						::save)
				.generateAdvancement()
				.build(consumer, Registration.UPGRADE_NOAI.getId());
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_FLIGHT.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.FEATHER)
				.define('e', Items.END_CRYSTAL)
				.define('a', Items.PHANTOM_MEMBRANE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_LAVAIMMUNITY.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.MAGMA_CREAM)
				.define('e', Items.LAVA_BUCKET)
				.define('a', Items.NETHERITE_SCRAP)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_PHASE.get())
				.pattern("aea")
				.pattern("fbf")
				.pattern("aea")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Items.DRAGON_BREATH)
				.define('e', Items.PHANTOM_MEMBRANE)
				.define('a', Items.SHULKER_SHELL)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_TIMEPROTECTION.get())
				.pattern(" s ")
				.pattern("fbf")
				.pattern(" s ")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('f', Registration.TimeCrystal.get())
				.define('s', Items.SHIELD)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_SPLASH.get())
				.pattern("p p")
				.pattern("gbg")
				.pattern("p p")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Items.GLASS_BOTTLE)
				.define('p', Items.GUNPOWDER)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_LINGERING.get())
				.pattern("p p")
				.pattern("gbg")
				.pattern("p p")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Items.GLASS_BOTTLE)
				.define('p', Items.DRAGON_BREATH)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_POTIONARROW.get())
				.pattern("p p")
				.pattern("gbg")
				.pattern("p p")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Items.GLASS_BOTTLE)
				.define('p', Items.NETHER_WART)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_HOMING.get())
				.pattern("p p")
				.pattern("gbg")
				.pattern("p p")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Items.TARGET)
				.define('p', Items.ENDER_EYE)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.UPGRADE_EPICARROW.get())
				.pattern("psp")
				.pattern("gbg")
				.pattern("psp")
				.define('b', Registration.UPGRADE_BASE.get())
				.define('g', Items.PHANTOM_MEMBRANE)
				.define('p', Items.ENDER_EYE)
				.define('s', Items.NETHER_STAR)
				.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
				.save(consumer);
		// GooSpread Recipes
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "dire_iron_block"), Blocks.IRON_BLOCK.defaultBlockState(), Registration.RawFerricoreOre.get().defaultBlockState(), 1, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t1", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier1_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "dire_gold_block"), Blocks.GOLD_BLOCK.defaultBlockState(), Registration.RawBlazegoldOre.get().defaultBlockState(), 2, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t2", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier2_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "dire_diamond_block"), Blocks.DIAMOND_BLOCK.defaultBlockState(), Registration.RawCelestigemOre.get().defaultBlockState(), 3, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "dire_netherite_block"), Blocks.NETHERITE_BLOCK.defaultBlockState(), Registration.RawEclipseAlloyOre.get().defaultBlockState(), 4, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier4_ITEM.get()))
				.save(consumer);

		// GooSpread Coal
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "coal_block_t1"), Blocks.COAL_BLOCK.defaultBlockState(), Registration.RawCoal_T1.get().defaultBlockState(), 1, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t1", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier1_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeTagBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "coal_block_t1"), JustDireBlockTags.CHARCOAL, Registration.RawCoal_T1.get().defaultBlockState(), 1, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t1", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier1_ITEM.get()))
				.save(consumer);

		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "coal_block_t2"), Registration.CoalBlock_T1.get().defaultBlockState(), Registration.RawCoal_T2.get().defaultBlockState(), 2, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t2", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier2_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "coal_block_t3"), Registration.CoalBlock_T2.get().defaultBlockState(), Registration.RawCoal_T3.get().defaultBlockState(), 3, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "coal_block_t4"), Registration.CoalBlock_T3.get().defaultBlockState(), Registration.RawCoal_T4.get().defaultBlockState(), 4, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier4_ITEM.get()))
				.save(consumer);

		// GooSpread Misc
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "portal_fluid"), Registration.UNSTABLE_PORTAL_FLUID_BLOCK.get().defaultBlockState(), Registration.PORTAL_FLUID_BLOCK.get().defaultBlockState(), 3, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "refined_t2_fluid"), Registration.UNREFINED_T2_FLUID_BLOCK.get().defaultBlockState(), Registration.REFINED_T2_FLUID_BLOCK.get().defaultBlockState(), 2, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t2", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier2_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "refined_t3_fluid"), Registration.UNREFINED_T3_FLUID_BLOCK.get().defaultBlockState(), Registration.REFINED_T3_FLUID_BLOCK.get().defaultBlockState(), 3, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier3_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "refined_t4_fluid"), Registration.UNREFINED_T4_FLUID_BLOCK.get().defaultBlockState(), Registration.REFINED_T4_FLUID_BLOCK.get().defaultBlockState(), 4, 2400)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier4_ITEM.get()))
				.save(consumer);
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "budding_time_amethyst"), Blocks.BUDDING_AMETHYST.defaultBlockState(), Registration.TimeCrystalBuddingBlock.get().defaultBlockState(), 4, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier4_ITEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "budding_time_amethyst"));
		GooSpreadRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "budding_time_timeblock"), Registration.TimeCrystalBlock.get().defaultBlockState(), Registration.TimeCrystalBuddingBlock.get().defaultBlockState(), 4, 4800)
				.group("justdirethings")
				.unlockedBy("has_goo_block_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.GooBlock_Tier4_ITEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "budding_time_timeblock"));

		// FluidDrop Recipes
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "polymorphic_fluid"), Blocks.WATER.defaultBlockState(), Registration.POLYMORPHIC_FLUID_BLOCK.get().defaultBlockState(), Registration.PolymorphicCatalyst.get())
				.group("justdirethings")
				.unlockedBy("has_polymorphic_catalyst", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.PolymorphicCatalyst.get()))
				.save(consumer);
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "unstable_portal_fluid"), Registration.POLYMORPHIC_FLUID_BLOCK.get().defaultBlockState(), Registration.UNSTABLE_PORTAL_FLUID_BLOCK.get().defaultBlockState(), Registration.PortalFluidCatalyst.get())
				.group("justdirethings")
				.unlockedBy("has_portal_catalyst", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.PortalFluidCatalyst.get()))
				.save(consumer);
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "unrefined_t2_fluid"), Registration.POLYMORPHIC_FLUID_BLOCK.get().defaultBlockState(), Registration.UNREFINED_T2_FLUID_BLOCK.get().defaultBlockState(), Registration.Coal_T2.get())
				.group("justdirethings")
				.unlockedBy("has_coal_t2", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Coal_T2.get()))
				.save(consumer);
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "unrefined_t3_fluid"), Registration.REFINED_T2_FLUID_BLOCK.get().defaultBlockState(), Registration.UNREFINED_T3_FLUID_BLOCK.get().defaultBlockState(), Registration.Coal_T3.get())
				.group("justdirethings")
				.unlockedBy("has_coal_t3", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Coal_T3.get()))
				.save(consumer);
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "unrefined_t4_fluid"), Registration.REFINED_T3_FLUID_BLOCK.get().defaultBlockState(), Registration.UNREFINED_T4_FLUID_BLOCK.get().defaultBlockState(), Registration.Coal_T4.get())
				.group("justdirethings")
				.unlockedBy("has_coal_t4", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Coal_T4.get()))
				.save(consumer);
		FluidDropRecipeBuilder.shapeless(ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "time_fluid"), Registration.POLYMORPHIC_FLUID_BLOCK.get().defaultBlockState(), Registration.TIME_FLUID_BLOCK.get().defaultBlockState(), Registration.TimeCrystal.get())
				.group("justdirethings")
				.unlockedBy("has_time_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TimeCrystal.get()))
				.save(consumer);

		// Smelting
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Registration.RawFerricore.get()),
						Registration.FerricoreIngot.get(), 1.0f, 200)
				.unlockedBy("has_ferricore_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawFerricore.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "ferricore_ingot_smelted"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Registration.RawBlazegold.get()),
						Registration.BlazegoldIngot.get(), 1.0f, 200)
				.unlockedBy("has_blazegold_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawBlazegold.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "blazegold_ingot_smelted"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Registration.RawEclipseAlloy.get()),
						Registration.EclipseAlloyIngot.get(), 1.0f, 200)
				.unlockedBy("has_eclipsealloy_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawEclipseAlloy.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "eclipsealloy_ingot_smelted"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Registration.RawFerricore.get()),
						Registration.FerricoreIngot.get(), 1.0f, 100)
				.unlockedBy("has_ferricore_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawFerricore.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "ferricore_ingot_blasted"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Registration.RawBlazegold.get()),
						Registration.BlazegoldIngot.get(), 1.0f, 100)
				.unlockedBy("has_blazegold_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawBlazegold.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "blazegold_ingot_blasted"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Registration.RawEclipseAlloy.get()),
						Registration.EclipseAlloyIngot.get(), 1.0f, 100)
				.unlockedBy("has_eclipsealloy_raw", inventoryTrigger(ItemPredicate.Builder.item().of(Registration.RawEclipseAlloy.get()).build()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "eclipsealloy_ingot_blasted"));

		// Tools
		ShapedRecipeBuilder.shaped(Registration.FerricoreSword.get())
				.pattern(" f ")
				.pattern(" f ")
				.pattern(" s ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricorePickaxe.get())
				.pattern("fff")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreShovel.get())
				.pattern(" f ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreAxe.get())
				.pattern("ff ")
				.pattern("fs ")
				.pattern(" s ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreHoe.get())
				.pattern("ff ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreBow.get())
				.pattern("sf ")
				.pattern("s f")
				.pattern("sf ")
				.define('f', Registration.FerricoreIngot.get())
				.define('s', Items.STRING)
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazegoldBow.get())
				.pattern("sf ")
				.pattern("s f")
				.pattern("sf ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STRING)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreBow.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldBow.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldBow.getId().getPath() + "-templateupgrade"));
		ShapedRecipeBuilder.shaped(Registration.CelestigemBow.get())
				.pattern("sf ")
				.pattern("s f")
				.pattern("sf ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STRING)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldBow.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemBow.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemBow.getId().getPath() + "-templateupgrade"));
		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyBow.get())
				.pattern("sf ")
				.pattern("s f")
				.pattern("sf ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STRING)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemBow.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyBow.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyBow.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.BlazegoldSword.get())
				.pattern(" f ")
				.pattern(" f ")
				.pattern(" s ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazegoldPickaxe.get())
				.pattern("fff")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazegoldShovel.get())
				.pattern(" f ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazegoldAxe.get())
				.pattern("ff ")
				.pattern("fs ")
				.pattern(" s ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.BlazegoldHoe.get())
				.pattern("ff ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.BlazegoldIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreSword.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldSword.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldSword.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricorePickaxe.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldPickaxe.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldPickaxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreShovel.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldShovel.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldShovel.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreAxe.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldAxe.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldAxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreHoe.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldHoe.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldHoe.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.CelestigemSword.get())
				.pattern(" f ")
				.pattern(" f ")
				.pattern(" s ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.CelestigemPickaxe.get())
				.pattern("fff")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.CelestigemShovel.get())
				.pattern(" f ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.CelestigemAxe.get())
				.pattern("ff ")
				.pattern("fs ")
				.pattern(" s ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.CelestigemHoe.get())
				.pattern("ff ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.Celestigem.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		PaxelRecipeBuilder.shapeless(Ingredient.of(Registration.CelestigemPickaxe.get()), Ingredient.of(Registration.CelestigemAxe.get()),
						Ingredient.of(Registration.CelestigemShovel.get()), Registration.CelestigemPaxel.get())
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemPaxel.getId().getPath()));

		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldSword.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemSword.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemSword.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldPickaxe.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemPickaxe.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemPickaxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldShovel.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemShovel.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemShovel.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldAxe.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemAxe.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemAxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldHoe.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemHoe.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemHoe.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.EclipseAlloySword.get())
				.pattern(" f ")
				.pattern(" f ")
				.pattern(" s ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyPickaxe.get())
				.pattern("fff")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyShovel.get())
				.pattern(" f ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyAxe.get())
				.pattern("ff ")
				.pattern("fs ")
				.pattern(" s ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyHoe.get())
				.pattern("ff ")
				.pattern(" s ")
				.pattern(" s ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.define('s', Items.STICK)
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		PaxelRecipeBuilder.shapeless(Ingredient.of(Registration.EclipseAlloyPickaxe.get()), Ingredient.of(Registration.EclipseAlloyAxe.get()),
						Ingredient.of(Registration.EclipseAlloyShovel.get()), Registration.EclipseAlloyPaxel.get())
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyPaxel.getId().getPath()));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemSword.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloySword.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloySword.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemPickaxe.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyPickaxe.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyPickaxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemShovel.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyShovel.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyShovel.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemAxe.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyAxe.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyAxe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemHoe.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyHoe.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyHoe.getId().getPath() + "-templateupgrade"));
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemPaxel.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyPaxel.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyPaxel.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.FerricoreBoots.get())
				.pattern("f f")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreLeggings.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("f f")
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreChestplate.get())
				.pattern("f f")
				.pattern("fff")
				.pattern("fff")
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(Registration.FerricoreHelmet.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.FerricoreIngot.get())
				.group("justdirethings")
				.unlockedBy("has_ferricore_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.FerricoreIngot.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(Registration.BlazegoldBoots.get())
				.pattern("f f")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreBoots.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldBoots.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldBoots.getId().getPath() + "-templateupgrade"));
		ShapedRecipeBuilder.shaped(Registration.BlazegoldLeggings.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("f f")
				.define('f', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreLeggings.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldLeggings.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldLeggings.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.BlazegoldChestplate.get())
				.pattern("f f")
				.pattern("fff")
				.pattern("fff")
				.define('f', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreChestplate.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldChestplate.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldChestplate.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.BlazegoldHelmet.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.BlazegoldIngot.get())
				.group("justdirethings")
				.unlockedBy("has_blazegold_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.BlazegoldIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_BLAZEGOLD.get()), Ingredient.of(Registration.FerricoreHelmet.get()),
						Ingredient.of(Registration.BlazegoldIngot.get()), Registration.BlazegoldHelmet.get())
				.unlocks("has_template_blazegold", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_BLAZEGOLD.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.BlazegoldHelmet.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.CelestigemBoots.get())
				.pattern("f f")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldBoots.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemBoots.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemBoots.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.CelestigemLeggings.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("f f")
				.define('f', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldLeggings.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemLeggings.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemLeggings.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.CelestigemChestplate.get())
				.pattern("f f")
				.pattern("fff")
				.pattern("fff")
				.define('f', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldChestplate.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemChestplate.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemChestplate.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.CelestigemHelmet.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.Celestigem.get())
				.group("justdirethings")
				.unlockedBy("has_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.Celestigem.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_CELESTIGEM.get()), Ingredient.of(Registration.BlazegoldHelmet.get()),
						Ingredient.of(Registration.Celestigem.get()), Registration.CelestigemHelmet.get())
				.unlocks("has_template_celestigem", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_CELESTIGEM.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.CelestigemHelmet.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyBoots.get())
				.pattern("f f")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemBoots.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyBoots.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyBoots.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyLeggings.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("f f")
				.define('f', Registration.EclipseAlloyIngot.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemLeggings.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyLeggings.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyLeggings.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyChestplate.get())
				.pattern("f f")
				.pattern("fff")
				.pattern("fff")
				.define('f', Registration.EclipseAlloyIngot.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemChestplate.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyChestplate.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyChestplate.getId().getPath() + "-templateupgrade"));

		ShapedRecipeBuilder.shaped(Registration.EclipseAlloyHelmet.get())
				.pattern("fff")
				.pattern("f f")
				.pattern("   ")
				.define('f', Registration.EclipseAlloyIngot.get())
				.group("justdirethings")
				.unlockedBy("has_eclipsealloy_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.EclipseAlloyIngot.get()))
				.save(consumer);
		TierUpgradeRecipeBuilder.smithing(Ingredient.of(Registration.TEMPLATE_ECLIPSEALLOY.get()), Ingredient.of(Registration.CelestigemHelmet.get()),
						Ingredient.of(Registration.EclipseAlloyIngot.get()), Registration.EclipseAlloyHelmet.get())
				.unlocks("has_template_eclipsealloy", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.TEMPLATE_ECLIPSEALLOY.get()))
				.save(consumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, Registration.EclipseAlloyHelmet.getId().getPath() + "-templateupgrade"));



		// Resource Conversions
		nineBlockStorageRecipes(consumer, Registration.FerricoreIngot.get(), Registration.FerricoreBlock.get(), Registration.FerricoreIngot.getId().toString() + "_9x9", "justdirethings", Registration.FerricoreBlock.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.BlazegoldIngot.get(), Registration.BlazeGoldBlock.get(), Registration.BlazegoldIngot.getId().toString() + "_9x9", "justdirethings", Registration.BlazeGoldBlock.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.Celestigem.get(), Registration.CelestigemBlock.get(), Registration.Celestigem.getId().toString() + "_9x9", "justdirethings", Registration.CelestigemBlock.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.EclipseAlloyIngot.get(), Registration.EclipseAlloyBlock.get(), Registration.EclipseAlloyIngot.getId().toString() + "_9x9", "justdirethings", Registration.EclipseAlloyBlock.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.Coal_T1.get(), Registration.CoalBlock_T1.get(), Registration.Coal_T1.getId().toString() + "_9x9", "justdirethings", Registration.CoalBlock_T1.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.Coal_T2.get(), Registration.CoalBlock_T2.get(), Registration.Coal_T2.getId().toString() + "_9x9", "justdirethings", Registration.CoalBlock_T2.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.Coal_T3.get(), Registration.CoalBlock_T3.get(), Registration.Coal_T3.getId().toString() + "_9x9", "justdirethings", Registration.CoalBlock_T3.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.Coal_T4.get(), Registration.CoalBlock_T4.get(), Registration.Coal_T4.getId().toString() + "_9x9", "justdirethings", Registration.CoalBlock_T4.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Registration.TimeCrystal.get(), Registration.TimeCrystalBlock.get(), Registration.TimeCrystal.getId().toString() + "_9x9", "justdirethings", Registration.TimeCrystalBlock.getId().toString() + "_9x9", "justdirethings");
		nineBlockStorageRecipes(consumer, Items.CHARCOAL, Registration.CharcoalBlock.get(), Registration.CharcoalBlock_ITEM.getId().toString() + "_9x9", "justdirethings", Registration.CharcoalBlock.getId().toString() + "_block_9x9", "justdirethings");

		// NBT Clear
		for (var sidedBlock : Registration.SIDEDBLOCKS.getEntries()) {
			if (sidedBlock.get() instanceof BaseMachineBlock baseMachineBlock) {
				if (sidedBlock.equals(Registration.ParadoxMachine))
					continue; //Skip paradox machine, no reseting paradox energy
				ShapelessRecipeBuilder.shapeless(sidedBlock.get())
						.requires(sidedBlock.get())
						.group("justdirethings")
						.unlockedBy("has_" + sidedBlock.getId().getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(sidedBlock.get()))
						.save(consumer, sidedBlock.getId() + "_nbtclear");
			}
		}
		for (var sidedBlock : Registration.BLOCKS.getEntries()) {
			if (sidedBlock.get() instanceof BaseMachineBlock baseMachineBlock) {
				if (sidedBlock.equals(Registration.DireSmithingTable))
					continue; //Keeps no settings to clear
				ShapelessRecipeBuilder.shapeless(sidedBlock.get())
						.requires(sidedBlock.get())
						.group("justdirethings")
						.unlockedBy("has_" + sidedBlock.getId().getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(sidedBlock.get()))
						.save(consumer, sidedBlock.getId() + "_nbtclear");
			}
		}

		registerUpgrades(consumer);
	}

	public void registerUpgrades(Consumer<FinishedRecipe> consumer) {
		for (var upgrade : Registration.UPGRADES.getEntries()) {
			Ability ability = Ability.getAbilityFromUpgradeItem(upgrade.get());
			if (ability != null) {
				for (var armor : Registration.ARMORS.getEntries()) {
					Item armorItem = armor.get();
					if (armorItem instanceof ToggleableTool toggleableTool) {
						EnumSet<Ability> abilities = toggleableTool.getAllAbilities();
						if (abilities.contains(ability)) {
							AbilityRecipeBuilder.shapeless(Ingredient.EMPTY, Ingredient.of(armor.get()), Ingredient.of(upgrade.get()))
									.group("justdirethings")
									.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
									.save(consumer);
						}
					}
				}
				for (var tool : Registration.TOOLS.getEntries()) {
					Item toolItem = tool.get();
					if (toolItem instanceof ToggleableTool toggleableTool) {
						EnumSet<Ability> abilities = toggleableTool.getAllAbilities();
						if (abilities.contains(ability)) {
							AbilityRecipeBuilder.shapeless(Ingredient.EMPTY, Ingredient.of(tool.get()), Ingredient.of(upgrade.get()))
									.group("justdirethings")
									.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
									.save(consumer);
						}
					}
				}
				for (var bow : Registration.BOWS.getEntries()) {
					Item bowItem = bow.get();
					if (bowItem instanceof ToggleableTool toggleableTool) {
						EnumSet<Ability> abilities = toggleableTool.getAllAbilities();
						if (abilities.contains(ability)) {
							AbilityRecipeBuilder.shapeless(Ingredient.EMPTY, Ingredient.of(bow.get()), Ingredient.of(upgrade.get()))
									.group("justdirethings")
									.unlockedBy("has_upgrade_base", InventoryChangeTrigger.TriggerInstance.hasItems(Registration.UPGRADE_BASE.get()))
									.save(consumer);
						}
					}
				}
			}
		}
	}
}
