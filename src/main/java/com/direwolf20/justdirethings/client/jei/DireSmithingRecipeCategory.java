package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.datagen.recipes.DireSmithingRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class DireSmithingRecipeCategory implements IRecipeCategory<DireSmithingRecipe> {
	public static final RecipeType<DireSmithingRecipe> TYPE = RecipeType.create(JustDireThings.MODID, "dire_smithing", DireSmithingRecipe.class);

	public static final int width = 108;
	public static final int height = 28;

	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;
	private final IDrawableStatic slot;
	private final IDrawableStatic arrow;

	public DireSmithingRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(width, height);
		icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.DireSmithingTable.get()));
		localizedName = Registration.DireSmithingTable.get().getName();
		slot = guiHelper.getSlotDrawable();
		this.arrow = JEIIntegration.createRecipeArrow(guiHelper);
	}

	@Override
	public RecipeType<DireSmithingRecipe> getRecipeType() {
		return TYPE;
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends DireSmithingRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}

	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(DireSmithingRecipe recipe, IRecipeSlotsView slotsView, PoseStack poseStack, double mouseX, double mouseY) {
		arrow.draw(poseStack, 61, 6);
	}

	/**
	 * Every slot lists every combination, linked so the output always matches the inputs.
	 */
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DireSmithingRecipe recipe, IFocusGroup focuses) {
		ItemStack[] templates = getItemsOrEmpty(recipe.getTemplate());
		ItemStack[] bases = getItemsOrEmpty(recipe.getBase());
		ItemStack[] additions = getItemsOrEmpty(recipe.getAddition());
		int combinations = templates.length * bases.length * additions.length;
		List<ItemStack> templateStacks = new ArrayList<>(combinations);
		List<ItemStack> baseStacks = new ArrayList<>(combinations);
		List<ItemStack> additionStacks = new ArrayList<>(combinations);
		List<ItemStack> outputs = new ArrayList<>(combinations);
		for (int i = 0; i < combinations; i++) {
			ItemStack template = templates[i % templates.length];
			ItemStack base = bases[i / templates.length % bases.length];
			ItemStack addition = additions[i / (templates.length * bases.length)];
			templateStacks.add(template);
			baseStacks.add(base);
			additionStacks.add(addition);
			outputs.add(recipe.assemble(new SimpleContainer(template, base, addition)));
		}

		IRecipeSlotBuilder templateSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6)
				.setBackground(slot, -1, -1)
				.addItemStacks(templateStacks);

		IRecipeSlotBuilder baseSlot = builder.addSlot(RecipeIngredientRole.INPUT, 19, 6)
				.setBackground(slot, -1, -1)
				.addItemStacks(baseStacks);

		IRecipeSlotBuilder additionSlot = builder.addSlot(RecipeIngredientRole.INPUT, 37, 6)
				.setBackground(slot, -1, -1)
				.addItemStacks(additionStacks);

		IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 6)
				.setBackground(slot, -1, -1)
				.addItemStacks(outputs);

		builder.createFocusLink(templateSlot, baseSlot, additionSlot, outputSlot);
	}

	// An empty ingredient still needs one entry, or there are no combinations
	private static ItemStack[] getItemsOrEmpty(Ingredient ingredient) {
		ItemStack[] items = ingredient.getItems();
		return items.length == 0 ? new ItemStack[]{ItemStack.EMPTY} : items;
	}
}
