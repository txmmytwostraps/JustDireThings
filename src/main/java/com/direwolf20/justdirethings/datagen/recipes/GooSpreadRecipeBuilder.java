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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class GooSpreadRecipeBuilder implements RecipeBuilder {
	@Nullable
	private String group;

	private final ResourceLocation id;
	protected final BlockState input;
	protected final BlockState output;
	protected final int tierRequirement;
	protected final int craftingDuration;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public GooSpreadRecipeBuilder(ResourceLocation id, BlockState input, BlockState output, int tierRequirement, int craftingDuration) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.tierRequirement = tierRequirement;
		this.craftingDuration = craftingDuration;
	}

	public static GooSpreadRecipeBuilder shapeless(ResourceLocation id, BlockState input, BlockState output, int tierRequirement, int craftingDuration) {
		return new GooSpreadRecipeBuilder(id, input, output, tierRequirement, craftingDuration);
	}

	@Override
	public GooSpreadRecipeBuilder unlockedBy(String pName, CriterionTriggerInstance pCriterion) {
		this.advancement.addCriterion(pName, pCriterion);
		return this;
	}

	@Override
	public GooSpreadRecipeBuilder group(@Nullable String pGroupName) {
		this.group = pGroupName;
		return this;
	}

	@Override
	public Item getResult() {
		return ItemStack.EMPTY.getItem();
	}

	@Override
	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
		this.save(pFinishedRecipeConsumer, ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID,
				ForgeRegistries.BLOCKS.getKey(this.output.getBlock()).getPath() + "-goospread"));
	}

	@Override
	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pId) {
		this.ensureValid(pId);
		this.advancement.parent(ResourceLocation.withDefaultNamespace("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
				.rewards(AdvancementRewards.Builder.recipe(pId))
				.requirements(RequirementsStrategy.OR);
		pFinishedRecipeConsumer.accept(new FinishedGooSpreadRecipe(pId, this.id, this.input, this.output,
				this.tierRequirement, this.craftingDuration, this.advancement,
				ResourceLocation.fromNamespaceAndPath(pId.getNamespace(), "recipes/misc/" + pId.getPath())));
	}

	private void ensureValid(ResourceLocation pId) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + pId);
		}
	}
}
