package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseAxe;
import com.direwolf20.justdirethings.common.items.tools.basetools.BasePickaxe;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseShovel;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Keeps the upgrades and enchantments of all three tools.
 */
public class PaxelRecipe implements DireSmithingRecipe {
	private final ResourceLocation id;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final ItemStack result;

	public PaxelRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition,
			ItemStack result) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
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
		ItemStack pickaxe = inv.getItem(0);
		ItemStack axe = inv.getItem(1);
		ItemStack shovel = inv.getItem(2);
		ItemStack result = getResultItem();

		if (isTemplateIngredient(pickaxe) && pickaxe.getItem() instanceof ToggleableTool pickaxetoggleableTool) {
			for (Ability ability : pickaxetoggleableTool.getAbilities()) {
				if (ToggleableTool.hasUpgrade(pickaxe, ability))
					result.getOrCreateTag().putBoolean("upgrade_" + ability.getName(), true);
			}
		} else {
			return ItemStack.EMPTY;
		}
		if (isBaseIngredient(axe) && axe.getItem() instanceof ToggleableTool axetoggleableTool) {
			for (Ability ability : axetoggleableTool.getAbilities()) {
				if (ToggleableTool.hasUpgrade(axe, ability))
					result.getOrCreateTag().putBoolean("upgrade_" + ability.getName(), true);
			}
		} else {
			return ItemStack.EMPTY;
		}
		if (isAdditionIngredient(shovel) && shovel.getItem() instanceof ToggleableTool shoveltoggleableTool) {
			for (Ability ability : shoveltoggleableTool.getAbilities()) {
				if (ToggleableTool.hasUpgrade(shovel, ability))
					result.getOrCreateTag().putBoolean("upgrade_" + ability.getName(), true);
			}
		} else {
			return ItemStack.EMPTY;
		}

		// The first tool wins any enchantment conflict
		Map<Enchantment, Integer> enchantments = new LinkedHashMap<>();
		for (ItemStack tool : List.of(pickaxe, axe, shovel)) {
			EnchantmentHelper.getEnchantments(tool).forEach((enchantment, level) -> {
				if (enchantments.keySet().stream().allMatch(existing -> existing.isCompatibleWith(enchantment)))
					enchantments.put(enchantment, level);
			});
		}
		EnchantmentHelper.setEnchantments(enchantments, result);

		return result;
	}

	@Override
	public ItemStack getResultItem() {
		return result.copy();
	}

	@Override
	public boolean isTemplateIngredient(ItemStack stack) {
		return stack.getItem() instanceof BasePickaxe;
	}

	@Override
	public boolean isBaseIngredient(ItemStack stack) {
		return stack.getItem() instanceof BaseAxe;
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		return stack.getItem() instanceof BaseShovel;
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
		return Registration.PAXEL_RECIPE_SERIALIZER.get();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PaxelRecipe> {
		@Override
		public PaxelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient template = Ingredient.fromJson(json.get("template"));
			Ingredient base = Ingredient.fromJson(json.get("base"));
			Ingredient addition = Ingredient.fromJson(json.get("addition"));
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			return new PaxelRecipe(recipeId, template, base, addition, result);
		}

		@Override
		public PaxelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient template = Ingredient.fromNetwork(buffer);
			Ingredient base = Ingredient.fromNetwork(buffer);
			Ingredient addition = Ingredient.fromNetwork(buffer);
			ItemStack result = buffer.readItem();
			return new PaxelRecipe(recipeId, template, base, addition, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, PaxelRecipe recipe) {
			recipe.template.toNetwork(buffer);
			recipe.base.toNetwork(buffer);
			recipe.addition.toNetwork(buffer);
			buffer.writeItem(recipe.result);
		}
	}
}
