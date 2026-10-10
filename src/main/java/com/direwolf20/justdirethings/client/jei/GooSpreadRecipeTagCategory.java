package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.datagen.JustDireItemTags;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipeTag;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class GooSpreadRecipeTagCategory implements IRecipeCategory<GooSpreadRecipeTag> {
	public static final RecipeType<GooSpreadRecipeTag> TYPE = RecipeType.create(JustDireThings.MODID, "goospreadrecipe_tag", GooSpreadRecipeTag.class);

	public static final int width = 120;
	public static final int height = 40;

	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;
	private final IDrawableStatic arrow;

	public GooSpreadRecipeTagCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(width, height);
		icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.GooBlock_Tier1.get()));
		localizedName = new TranslatableComponent("justdirethings.goospreadrecipetag.title");
		this.arrow = JEIIntegration.createRecipeArrow(guiHelper);
	}

	@Override
	public RecipeType<GooSpreadRecipeTag> getRecipeType() {
		return TYPE;
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends GooSpreadRecipeTag> getRecipeClass() {
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
	public void draw(GooSpreadRecipeTag recipe, IRecipeSlotsView slotsView, PoseStack poseStack, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		arrow.draw(poseStack, 54, 12);
		background.draw(poseStack, 17, 0);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GooSpreadRecipeTag recipe, IFocusGroup focuses) {
		TagKey<Block> input = recipe.getInput();
		IRecipeSlotBuilder inputSlotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, 9, 12);
		List<ItemStack> itemstacks = getBlockTagItems(input);
		inputSlotBuilder.addItemStacks(itemstacks);

		builder.addSlot(RecipeIngredientRole.CATALYST, 29, 12)
				.addIngredients(
						Ingredient.of(
								JustDireItemTags.GOO_RECIPE_TIERS.get(recipe.getTierRequirement()-1)));

		BlockState output = recipe.getOutput();
		if (output.getBlock().asItem() != Items.AIR) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 12)
					.addItemStack(new ItemStack(output.getBlock()));
		} else if (output.getBlock() instanceof LiquidBlock liquidBlock) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 12)
					.addFluidStack(liquidBlock.getFluid(), 1000);
		}
	}

	/**
	 * Blocks without an item are skipped. An empty tag shows a barrier, like an empty item tag.
	 */
	private static List<ItemStack> getBlockTagItems(TagKey<Block> tag) {
		List<ItemStack> itemstacks = new ArrayList<>();
		for (Block block : ForgeRegistries.BLOCKS.tags().getTag(tag)) {
			ItemStack stack = new ItemStack(block);
			if (!stack.isEmpty())
				itemstacks.add(stack);
		}
		if (itemstacks.isEmpty())
			itemstacks.add(new ItemStack(Blocks.BARRIER).setHoverName(new TextComponent("Empty Tag: " + tag.location())));
		return itemstacks;
	}
}
