package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;

public class FinishedGooSpreadRecipe implements FinishedRecipe {
	private final ResourceLocation id;
	private final ResourceLocation recipeId;
	private final BlockState input;
	private final BlockState output;
	private final int tierRequirement;
	private final int craftingDuration;
	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	public FinishedGooSpreadRecipe(ResourceLocation id, ResourceLocation recipeId, BlockState input, BlockState output,
			int tierRequirement, int craftingDuration, Advancement.Builder advancement, ResourceLocation advancementId) {
		this.id = id;
		this.recipeId = recipeId;
		this.input = input;
		this.output = output;
		this.tierRequirement = tierRequirement;
		this.craftingDuration = craftingDuration;
		this.advancement = advancement;
		this.advancementId = advancementId;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("id", recipeId.toString());
		json.add("input", BlockState.CODEC.encodeStart(JsonOps.INSTANCE, input).getOrThrow(false, e -> {
		}));
		json.add("output", BlockState.CODEC.encodeStart(JsonOps.INSTANCE, output).getOrThrow(false, e -> {
		}));
		json.addProperty("tierRequirement", tierRequirement);
		json.addProperty("craftingDuration", craftingDuration);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getType() {
		return Registration.GOO_SPREAD_RECIPE_SERIALIZER.get();
	}

	@Override
	public JsonObject serializeAdvancement() {
		return advancement.serializeToJson();
	}

	@Override
	public ResourceLocation getAdvancementId() {
		return advancementId;
	}
}
