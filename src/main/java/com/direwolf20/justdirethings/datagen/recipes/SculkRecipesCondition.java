package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Config;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * True when use_sculk_recipes is on. The vanilla fallbacks use it inverted.
 */
public class SculkRecipesCondition implements ICondition {
	private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "use_sculk_recipes");
	public static final SculkRecipesCondition INSTANCE = new SculkRecipesCondition();

	private SculkRecipesCondition() {
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return Config.USE_SCULK_RECIPES.get();
	}

	@Override
	public String toString() {
		return "use_sculk_recipes";
	}

	public static class Serializer implements IConditionSerializer<SculkRecipesCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, SculkRecipesCondition value) {
		}

		@Override
		public SculkRecipesCondition read(JsonObject json) {
			return SculkRecipesCondition.INSTANCE;
		}

		@Override
		public ResourceLocation getID() {
			return SculkRecipesCondition.NAME;
		}
	}
}
