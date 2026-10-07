package com.direwolf20.justdirethings.datagen.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class TierUpgradeRecipeBuilder {
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final Item result;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public TierUpgradeRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition, Item result) {
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public static TierUpgradeRecipeBuilder smithing(Ingredient template, Ingredient base, Ingredient addition,
			Item result) {
		return new TierUpgradeRecipeBuilder(template, base, addition, result);
	}

	public TierUpgradeRecipeBuilder unlocks(String pName, CriterionTriggerInstance pCriterion) {
		this.advancement.addCriterion(pName, pCriterion);
		return this;
	}

	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pId) {
		this.ensureValid(pId);
		this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
				.rewards(AdvancementRewards.Builder.recipe(pId))
				.requirements(RequirementsStrategy.OR);
		pFinishedRecipeConsumer.accept(new FinishedTierUpgradeRecipe(pId, this.template, this.base, this.addition,
				this.result, this.advancement,
				ResourceLocation.fromNamespaceAndPath(pId.getNamespace(), "recipes/misc/" + pId.getPath())));
	}

	private void ensureValid(ResourceLocation pId) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + pId);
		}
	}
}
