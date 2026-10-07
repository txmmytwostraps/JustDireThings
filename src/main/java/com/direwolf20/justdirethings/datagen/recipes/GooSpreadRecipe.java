package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.common.blockentities.basebe.GooBlockBE_Base;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class GooSpreadRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	protected final BlockState input;
	protected final BlockState output;
	protected int tierRequirement;
	protected int craftingDuration;

	public GooSpreadRecipe(ResourceLocation id, BlockState input, BlockState output, int tierRequirement, int craftingDuration) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.tierRequirement = tierRequirement;
		this.craftingDuration = craftingDuration;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return Registration.GOO_SPREAD_RECIPE_TYPE.get();
	}

	public boolean matches(GooBlockBE_Base gooBlockBE_base, BlockState sourceState) {
		return sourceState.equals(input) && gooBlockBE_base.getTier() >= tierRequirement;
	}

	public BlockState getOutput() {
		return output;
	}

	public BlockState getInput() {
		return input;
	}

	public int getTierRequirement() {
		return tierRequirement;
	}

	public int getCraftingDuration() {
		return craftingDuration;
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
		return Registration.GOO_SPREAD_RECIPE_SERIALIZER.get();
	}

	static BlockState parseBlockState(JsonElement json) {
		return BlockState.CODEC.parse(JsonOps.INSTANCE, json).result()
				.orElseThrow(() -> new JsonSyntaxException("Invalid block state: " + json));
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<GooSpreadRecipe> {
		@Override
		public GooSpreadRecipe fromJson(ResourceLocation id, JsonObject json) {
			BlockState inputState = parseBlockState(GsonHelper.getAsJsonObject(json, "input"));
			BlockState outputState = parseBlockState(GsonHelper.getAsJsonObject(json, "output"));
			int tierRequirement = GsonHelper.getAsInt(json, "tierRequirement");
			int craftingDuration = GsonHelper.getAsInt(json, "craftingDuration");
			return new GooSpreadRecipe(id, inputState, outputState, tierRequirement, craftingDuration);
		}

		@Override
		public GooSpreadRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf pBuffer) {
			BlockState inputState = Block.stateById(pBuffer.readInt());
			BlockState outputState = Block.stateById(pBuffer.readInt());
			int tierRequirement = pBuffer.readInt();
			int craftingDuration = pBuffer.readInt();

			return new GooSpreadRecipe(id, inputState, outputState, tierRequirement, craftingDuration);
		}

		@Override
		public void toNetwork(FriendlyByteBuf pBuffer, GooSpreadRecipe pRecipe) {
			pBuffer.writeInt(Block.getId(pRecipe.input));
			pBuffer.writeInt(Block.getId(pRecipe.output));
			pBuffer.writeInt(pRecipe.tierRequirement);
			pBuffer.writeInt(pRecipe.craftingDuration);
		}
	}
}
