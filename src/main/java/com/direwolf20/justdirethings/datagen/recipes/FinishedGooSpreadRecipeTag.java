package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FinishedGooSpreadRecipeTag implements FinishedRecipe {
	private final ResourceLocation id;
	private final ResourceLocation recipeId;
	private final TagKey<Block> input;
	private final BlockState output;
	private final int tierRequirement;
	private final int craftingDuration;
	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	public FinishedGooSpreadRecipeTag(ResourceLocation id, ResourceLocation recipeId, TagKey<Block> input,
			BlockState output, int tierRequirement, int craftingDuration, Advancement.Builder advancement,
			ResourceLocation advancementId) {
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
		JsonObject inputJson = new JsonObject();
		inputJson.addProperty("tag", input.location().toString());
		json.add("input", inputJson);
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
		return Registration.GOO_SPREAD_RECIPE_SERIALIZER_TAG.get();
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
