package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FluidDropRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	protected final BlockState input;
	protected final BlockState output;
	protected final Item catalyst;

	public FluidDropRecipe(ResourceLocation id, BlockState input, BlockState output, Item catalyst) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.catalyst = catalyst;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return Registration.FLUID_DROP_RECIPE_TYPE.get();
	}

	public boolean matches(BlockState blockState, ItemStack catalystStack) {
		if (!catalystStack.is(catalyst)) return false;
		return blockState.getFluidState().is(input.getFluidState().getType()) && blockState.getFluidState().isSource();
	}

	public BlockState getOutput() {
		return output;
	}

	public BlockState getInput() {
		return input;
	}

	public Item getCatalyst() {
		return catalyst;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean matches(CraftingContainer pInv, Level pLevel) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer pContainer) {
		return ItemStack.EMPTY;
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.FLUID_DROP_RECIPE_SERIALIZER.get();
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FluidDropRecipe> {
		@Override
		public FluidDropRecipe fromJson(ResourceLocation id, JsonObject json) {
			BlockState inputState = GooSpreadRecipe.parseBlockState(GsonHelper.getAsJsonObject(json, "input"));
			BlockState outputState = GooSpreadRecipe.parseBlockState(GsonHelper.getAsJsonObject(json, "output"));
			Item catalyst = GsonHelper.getAsItem(json, "catalyst");
			return new FluidDropRecipe(id, inputState, outputState, catalyst);
		}

		@Override
		public FluidDropRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			BlockState inputState = Block.stateById(buf.readInt());
			BlockState outputState = Block.stateById(buf.readInt());
			Item catalyst = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
			return new FluidDropRecipe(id, inputState, outputState, catalyst);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, FluidDropRecipe recipe) {
			buf.writeInt(Block.getId(recipe.input));
			buf.writeInt(Block.getId(recipe.output));
			buf.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.catalyst));
		}
	}
}
