package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.common.items.abilityupgrades.Upgrade;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.stream.Stream;

public class AbilityRecipe implements DireSmithingRecipe {
	private final ResourceLocation id;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;

	public AbilityRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean matches(Container inv, Level level) {
		return template.test(inv.getItem(0)) && base.test(inv.getItem(1)) && addition.test(inv.getItem(2));
	}

	@Override
	public ItemStack assemble(Container inv) {
		ItemStack baseStack = inv.getItem(1);
		ItemStack upgradeStack = inv.getItem(2);
		if (isBaseIngredient(baseStack) && baseStack.getItem() instanceof ToggleableTool toggleableTool) {
			Ability ability = Ability.getAbilityFromUpgradeItem(upgradeStack.getItem());
			if (ability != null && toggleableTool.hasAbility(ability) && !ToggleableTool.hasUpgrade(baseStack, ability)
					&& Config.AVAILABLE_ABILITY_MAP.get(ability).get()) {
				ItemStack result = baseStack.copy();
				result.setCount(1);
				result.getOrCreateTag().putBoolean("upgrade_" + ability.getName(), true);
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResultItem() {
		ItemStack[] baseItems = base.getItems();
		ItemStack[] additionItems = addition.getItems();
		if (baseItems.length == 0 || additionItems.length == 0)
			return ItemStack.EMPTY;
		Ability ability = Ability.getAbilityFromUpgradeItem(additionItems[0].getItem());
		if (ability == null)
			return ItemStack.EMPTY;
		if (Config.SERVER_CONFIG.isLoaded() && !Config.AVAILABLE_ABILITY_MAP.get(ability).get())
			return ItemStack.EMPTY;
		ItemStack itemstack = new ItemStack(baseItems[0].getItem());
		itemstack.getOrCreateTag().putBoolean("upgrade_" + ability.getName(), true);
		return itemstack;
	}

	@Override
	public boolean isTemplateIngredient(ItemStack stack) {
		return stack.isEmpty();
	}

	@Override
	public boolean isBaseIngredient(ItemStack stack) {
		return stack.getItem() instanceof ToggleableTool;
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		return stack.getItem() instanceof Upgrade;
	}

	@Override
	public boolean isIncomplete() {
		return Stream.of(template, base, addition).anyMatch(ForgeHooks::hasNoElements);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.ABILITY_RECIPE_SERIALIZER.get();
	}

	public Ingredient getTemplate() {
		return template;
	}

	public Ingredient getBase() {
		return base;
	}

	public Ingredient getAddition() {
		return addition;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AbilityRecipe> {
		@Override
		public AbilityRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			// Ingredient.fromJson rejects the empty template array
			Ingredient template;
			if (!json.has("template")
					|| (json.get("template").isJsonArray() && json.getAsJsonArray("template").size() == 0)) {
				template = Ingredient.EMPTY;
			} else {
				template = Ingredient.fromJson(json.get("template"));
			}
			Ingredient base = Ingredient.fromJson(json.get("base"));
			Ingredient addition = Ingredient.fromJson(json.get("addition"));
			return new AbilityRecipe(recipeId, template, base, addition);
		}

		@Override
		public AbilityRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient template = Ingredient.fromNetwork(buffer);
			Ingredient base = Ingredient.fromNetwork(buffer);
			Ingredient addition = Ingredient.fromNetwork(buffer);
			return new AbilityRecipe(recipeId, template, base, addition);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AbilityRecipe recipe) {
			recipe.template.toNetwork(buffer);
			recipe.base.toNetwork(buffer);
			recipe.addition.toNetwork(buffer);
		}
	}
}
