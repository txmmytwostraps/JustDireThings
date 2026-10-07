package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AbilityRecipeBuilder implements RecipeBuilder {
	@Nullable
	private String group;

	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public AbilityRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition) {
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public static AbilityRecipeBuilder shapeless(Ingredient template, Ingredient base, Ingredient addition) {
		return new AbilityRecipeBuilder(template, base, addition);
	}

	@Override
	public AbilityRecipeBuilder unlockedBy(String pName, CriterionTriggerInstance pCriterion) {
		this.advancement.addCriterion(pName, pCriterion);
		return this;
	}

	@Override
	public AbilityRecipeBuilder group(@Nullable String pGroupName) {
		this.group = pGroupName;
		return this;
	}

	@Override
	public Item getResult() {
		return ItemStack.EMPTY.getItem();
	}

	@Override
	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
		String armorName = ForgeRegistries.ITEMS.getKey(this.base.getItems()[0].getItem()).getPath();
		String abilityItemPath = ForgeRegistries.ITEMS.getKey(this.addition.getItems()[0].getItem()).getPath();
		String abilityName = abilityItemPath.substring(abilityItemPath.indexOf("_") + 1);
		this.save(pFinishedRecipeConsumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, armorName + "-" + abilityName));
	}

	@Override
	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pId) {
		this.ensureValid(pId);
		this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
				.rewards(AdvancementRewards.Builder.recipe(pId))
				.requirements(RequirementsStrategy.OR);
		pFinishedRecipeConsumer.accept(new FinishedAbilityRecipe(pId, this.template, this.base, this.addition,
				this.advancement, ResourceLocation.fromNamespaceAndPath(pId.getNamespace(), "recipes/misc/" + pId.getPath())));
	}

	private void ensureValid(ResourceLocation pId) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + pId);
		}
	}
}
