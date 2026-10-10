package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.jei.ghostfilters.GhostFilterBasic;
import com.direwolf20.justdirethings.client.screens.DireSmithingTableScreen;
import com.direwolf20.justdirethings.client.screens.basescreens.BaseScreen;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.DireSmithingTableContainer;
import com.direwolf20.justdirethings.datagen.recipes.DireSmithingRecipe;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipe;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipe;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipeTag;
import com.direwolf20.justdirethings.setup.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
	private static final ResourceLocation JEI_GUI_VANILLA = ResourceLocation.fromNamespaceAndPath(ModIds.JEI_ID, "textures/gui/gui_vanilla.png");

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "jei_plugin");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		IRecipeManager recipeRegistry = jeiRuntime.getRecipeManager();
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		List<CraftingRecipe> hiddenRecipes = new ArrayList<>();
		for (var sidedBlock : Registration.SIDEDBLOCKS.getEntries()) {
			if (sidedBlock.get() instanceof BaseMachineBlock) {
				Optional<? extends Recipe<?>> recipe = recipeManager.byKey(ResourceLocation.parse(sidedBlock.getId() + "_nbtclear"));
				recipe.ifPresent(r -> hiddenRecipes.add((CraftingRecipe) r));
			}
		}
		for (var sidedBlock : Registration.BLOCKS.getEntries()) {
			if (sidedBlock.get() instanceof BaseMachineBlock) {
				Optional<? extends Recipe<?>> recipe = recipeManager.byKey(ResourceLocation.parse(sidedBlock.getId() + "_nbtclear"));
				recipe.ifPresent(r -> hiddenRecipes.add((CraftingRecipe) r));
			}
		}

		recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				new GooSpreadRecipeCategory(guiHelper),
				new GooSpreadRecipeTagCategory(guiHelper),
				new FluidDropRecipeCategory(guiHelper)
		);
		registration.addRecipeCategories(new OreToResourceCategory(guiHelper));
		registration.addRecipeCategories(new DireSmithingRecipeCategory(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		assert Minecraft.getInstance().level != null;
		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		List<GooSpreadRecipe> goospreadrecipes = recipeManager.getAllRecipesFor(Registration.GOO_SPREAD_RECIPE_TYPE.get());

		registration.addRecipes(GooSpreadRecipeCategory.TYPE, goospreadrecipes);

		List<GooSpreadRecipeTag> goospreadtagrecipes = recipeManager.getAllRecipesFor(Registration.GOO_SPREAD_RECIPE_TYPE_TAG.get());

		registration.addRecipes(GooSpreadRecipeTagCategory.TYPE, goospreadtagrecipes);

		List<FluidDropRecipe> fluidDropRecipes = recipeManager.getAllRecipesFor(Registration.FLUID_DROP_RECIPE_TYPE.get());

		registration.addRecipes(FluidDropRecipeCategory.TYPE, fluidDropRecipes);

		// Hide recipes that make nothing, like upgrades for abilities disabled in config
		List<DireSmithingRecipe> direSmithingRecipes = recipeManager.getAllRecipesFor(Registration.DIRE_SMITHING_RECIPE_TYPE.get()).stream()
				.filter(recipe -> !recipe.getResultItem().isEmpty())
				.toList();

		registration.addRecipes(DireSmithingRecipeCategory.TYPE, direSmithingRecipes);

		//Ore to Resources
		registration.addRecipes(new RecipeType<>(OreToResourceCategory.UID, OreToResourceRecipe.class),
				List.of(new OreToResourceRecipe(Registration.RawFerricoreOre.get(), new ItemStack(Registration.RawFerricore.get())),
						new OreToResourceRecipe(Registration.RawBlazegoldOre.get(), new ItemStack(Registration.RawBlazegold.get())),
						new OreToResourceRecipe(Registration.RawCelestigemOre.get(), new ItemStack(Registration.Celestigem.get())),
						new OreToResourceRecipe(Registration.RawEclipseAlloyOre.get(), new ItemStack(Registration.RawEclipseAlloy.get())),
						new OreToResourceRecipe(Registration.RawCoal_T1.get(), new ItemStack(Registration.Coal_T1.get())),
						new OreToResourceRecipe(Registration.RawCoal_T2.get(), new ItemStack(Registration.Coal_T2.get())),
						new OreToResourceRecipe(Registration.RawCoal_T3.get(), new ItemStack(Registration.Coal_T3.get())),
						new OreToResourceRecipe(Registration.RawCoal_T4.get(), new ItemStack(Registration.Coal_T4.get()))
				));

	}

	// Inputs are slots 0-2, the output 3, then the player's 36
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(DireSmithingTableContainer.class, DireSmithingRecipeCategory.TYPE, 0, 3, 4, 36);
		registration.addRecipeTransferHandler(new UpgradeRecipeTransferInfo());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier1.get()), GooSpreadRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier2.get()), GooSpreadRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier3.get()), GooSpreadRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier4.get()), GooSpreadRecipeCategory.TYPE);

		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier1.get()), GooSpreadRecipeTagCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier2.get()), GooSpreadRecipeTagCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier3.get()), GooSpreadRecipeTagCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(Registration.GooBlock_Tier4.get()), GooSpreadRecipeTagCategory.TYPE);

		registry.addRecipeCatalyst(new ItemStack(Registration.DireSmithingTable.get()), DireSmithingRecipeCategory.TYPE, RecipeTypes.SMITHING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGhostIngredientHandler(BaseScreen.class, new GhostFilterBasic());
		registration.addRecipeClickArea(DireSmithingTableScreen.class, 68, 49, 22, 15, DireSmithingRecipeCategory.TYPE, RecipeTypes.SMITHING);
	}

	/**
	 * JEI's recipe arrow, cut from its vanilla texture. The gui helper has none.
	 * It keeps a gray background, so only draw it on gray.
	 */
	public static IDrawableStatic createRecipeArrow(IGuiHelper guiHelper) {
		return guiHelper.createDrawable(JEI_GUI_VANILLA, 61, 78, 22, 16);
	}

}
