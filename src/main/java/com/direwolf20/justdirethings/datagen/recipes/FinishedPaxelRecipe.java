package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class FinishedPaxelRecipe implements FinishedRecipe {
	private final ResourceLocation id;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final Item result;
	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	public FinishedPaxelRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition,
			Item result, Advancement.Builder advancement, ResourceLocation advancementId) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
		this.advancement = advancement;
		this.advancementId = advancementId;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("template", template.toJson());
		json.add("base", base.toJson());
		json.add("addition", addition.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
		json.add("result", resultJson);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getType() {
		return Registration.PAXEL_RECIPE_SERIALIZER.get();
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
