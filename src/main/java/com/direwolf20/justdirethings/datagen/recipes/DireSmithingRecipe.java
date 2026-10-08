package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Vanilla smithing has no template slot, so these get their own recipe type.
 */
public interface DireSmithingRecipe extends Recipe<Container> {
	@Override
	default RecipeType<?> getType() {
		return Registration.DIRE_SMITHING_RECIPE_TYPE.get();
	}

	// No recipe book can show these
	@Override
	default boolean isSpecial() {
		return true;
	}

	@Override
	default ItemStack getToastSymbol() {
		return new ItemStack(Registration.DireSmithingTable.get());
	}

	boolean isTemplateIngredient(ItemStack stack);

	boolean isBaseIngredient(ItemStack stack);

	boolean isAdditionIngredient(ItemStack stack);

	Ingredient getTemplate();

	Ingredient getBase();

	Ingredient getAddition();
}
