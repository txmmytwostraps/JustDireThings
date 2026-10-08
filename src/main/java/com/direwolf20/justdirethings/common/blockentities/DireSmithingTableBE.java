package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.capabilities.DireSmithingTableItemHandler;
import com.direwolf20.justdirethings.datagen.recipes.DireSmithingRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DireSmithingTableBE extends BaseMachineBE {
	public static final int TEMPLATE_SLOT = 0;
	public static final int BASE_SLOT = 1;
	public static final int ADDITION_SLOT = 2;
	public static final int OUTPUT_SLOT = 3;
	// Current order first, so items only move when they must
	private static final int[][] INPUT_ORDERS = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};

	// True on load, so a table saved mid-craft still crafts
	private boolean contentsChanged = true;
	private LazyOptional<IItemHandler> lazySmithingHandler = LazyOptional.empty();

	public DireSmithingTableBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		MACHINE_SLOTS = 4;
	}

	public DireSmithingTableBE(BlockPos pPos, BlockState pBlockState) {
		this(Registration.DireSmithingTableBE.get(), pPos, pBlockState);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (!lazySmithingHandler.isPresent())
				lazySmithingHandler = LazyOptional.of(this::getSmithingHandler);
			return lazySmithingHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazySmithingHandler.invalidate();
	}

	public IItemHandler getSmithingHandler() {
		return new DireSmithingTableItemHandler(this, getMachineHandler());
	}

	@Override
	public ItemStackHandler getMachineHandler() {
		if (machineHandler == null) {
			machineHandler = new ItemStackHandler(MACHINE_SLOTS) {
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return isValidInput(slot, stack);
				}

				@Override
				protected void onContentsChanged(int slot) {
					contentsChanged = true;
					setChanged();
				}
			};
		}
		return machineHandler;
	}

	// Craft on tick. Menus briefly empty slots while sizing stacks.
	@Override
	public void tickServer() {
		super.tickServer();
		if (contentsChanged) {
			contentsChanged = false;
			craft();
		}
	}

	public List<DireSmithingRecipe> getRecipes() {
		return level.getRecipeManager().getAllRecipesFor(Registration.DIRE_SMITHING_RECIPE_TYPE.get());
	}

	// Vanilla smithing recipes also craft when the template slot is empty
	public List<UpgradeRecipe> getUpgradeRecipes() {
		return level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
	}

	// Nothing goes into the output by hand
	public boolean isValidInput(int slot, ItemStack stack) {
		return switch (slot) {
			case TEMPLATE_SLOT -> getRecipes().stream().anyMatch(recipe -> recipe.isTemplateIngredient(stack));
			case BASE_SLOT -> getRecipes().stream().anyMatch(recipe -> recipe.isBaseIngredient(stack)) || isUpgradeInput(slot, stack);
			case ADDITION_SLOT -> getRecipes().stream().anyMatch(recipe -> recipe.isAdditionIngredient(stack)) || isUpgradeInput(slot, stack);
			default -> false;
		};
	}

	public boolean isUpgradeInput(int slot, ItemStack stack) {
		return switch (slot) {
			case BASE_SLOT -> getUpgradeRecipes().stream().anyMatch(recipe -> recipe.base.test(stack));
			case ADDITION_SLOT -> getUpgradeRecipes().stream().anyMatch(recipe -> recipe.isAdditionIngredient(stack));
			default -> false;
		};
	}

	public boolean isRecipeIngredient(ItemStack stack) {
		return getRecipeSlots().anyMatch(recipe -> recipe.isIngredient(stack));
	}

	public boolean acceptsInputs(ItemStack[] inputs) {
		return getRecipeSlots().anyMatch(recipe -> recipe.accepts(inputs));
	}

	public boolean acceptsUpgradeInputs(ItemStack[] inputs) {
		return getUpgradeRecipes().stream().map(RecipeSlots::of).anyMatch(recipe -> recipe.accepts(inputs));
	}

	// Our own recipes come first
	private Stream<RecipeSlots> getRecipeSlots() {
		return Stream.concat(getRecipes().stream().map(RecipeSlots::of), getUpgradeRecipes().stream().map(RecipeSlots::of));
	}

	/**
	 * Puts the stack wherever a recipe can still match, moving inputs around if needed. Returns the leftover.
	 */
	public ItemStack insertSorted(ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		ItemStack[] inputs = getInputs();
		if (Arrays.stream(inputs).noneMatch(input -> input.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, input)))
			return stack;
		List<RecipeSlots> recipes = getRecipeSlots().filter(recipe -> recipe.isIngredient(stack)).toList();
		if (recipes.isEmpty())
			return stack;
		// Top up a matching stack before rearranging
		for (int slot = 0; slot < OUTPUT_SLOT; slot++) {
			if (!inputs[slot].isEmpty() && ItemHandlerHelper.canItemStacksStack(stack, inputs[slot])
					&& getRoom(slot, inputs[slot], stack) > 0 && canBecomeRecipe(inputs, recipes))
				return insertInto(slot, inputs, stack, simulate);
		}
		for (int[] order : INPUT_ORDERS) {
			ItemStack[] arranged = new ItemStack[OUTPUT_SLOT];
			for (int slot = 0; slot < OUTPUT_SLOT; slot++)
				arranged[order[slot]] = inputs[slot];
			for (int slot = 0; slot < OUTPUT_SLOT; slot++) {
				if (!arranged[slot].isEmpty())
					continue;
				arranged[slot] = stack;
				boolean fits = canBecomeRecipe(arranged, recipes);
				arranged[slot] = ItemStack.EMPTY;
				if (fits)
					return insertInto(slot, arranged, stack, simulate);
			}
		}
		return stack;
	}

	private ItemStack insertInto(int slot, ItemStack[] arranged, ItemStack stack, boolean simulate) {
		ItemStack existing = arranged[slot];
		int inserted = Math.min(stack.getCount(), getRoom(slot, existing, stack));
		if (!simulate) {
			ItemStackHandler handler = getMachineHandler();
			for (int i = 0; i < OUTPUT_SLOT; i++) {
				if (i != slot)
					handler.setStackInSlot(i, arranged[i]);
			}
			handler.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(stack, existing.getCount() + inserted));
		}
		return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - inserted);
	}

	private int getRoom(int slot, ItemStack existing, ItemStack stack) {
		return Math.min(getMachineHandler().getSlotLimit(slot), stack.getMaxStackSize()) - existing.getCount();
	}

	/**
	 * True if a recipe can still match. A full set must actually craft something.
	 */
	private boolean canBecomeRecipe(ItemStack[] inputs, List<RecipeSlots> recipes) {
		boolean complete = false;
		for (RecipeSlots recipe : recipes) {
			if (!recipe.accepts(inputs))
				continue;
			if (!recipe.isComplete(inputs))
				return true;
			complete = true;
		}
		return complete && !getCraftResult(new SimpleContainer(inputs)).isEmpty();
	}

	/**
	 * Picks a recipe like the vanilla smithing menu. Ours first, then vanilla ones when there's no template.
	 */
	public ItemStack getCraftResult(Container inputs) {
		List<DireSmithingRecipe> recipes = level.getRecipeManager().getRecipesFor(Registration.DIRE_SMITHING_RECIPE_TYPE.get(), inputs, level);
		if (!recipes.isEmpty())
			return recipes.get(0).assemble(inputs);
		if (!inputs.getItem(TEMPLATE_SLOT).isEmpty())
			return ItemStack.EMPTY;
		Container upgradeInputs = new SimpleContainer(inputs.getItem(BASE_SLOT), inputs.getItem(ADDITION_SLOT));
		List<UpgradeRecipe> upgradeRecipes = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, upgradeInputs, level);
		return upgradeRecipes.isEmpty() ? ItemStack.EMPTY : upgradeRecipes.get(0).assemble(upgradeInputs);
	}

	// Same as taking the vanilla smithing result
	private void craft() {
		ItemStackHandler handler = getMachineHandler();
		if (!handler.getStackInSlot(OUTPUT_SLOT).isEmpty())
			return;
		ItemStack result = getCraftResult(new SimpleContainer(getInputs()));
		if (result.isEmpty())
			return;
		handler.setStackInSlot(OUTPUT_SLOT, result);
		for (int slot = 0; slot < OUTPUT_SLOT; slot++)
			handler.extractItem(slot, 1, false);
		level.levelEvent(LevelEvent.SOUND_SMITHING_TABLE_USED, getBlockPos(), 0);
	}

	private ItemStack[] getInputs() {
		ItemStackHandler handler = getMachineHandler();
		return new ItemStack[]{handler.getStackInSlot(TEMPLATE_SLOT), handler.getStackInSlot(BASE_SLOT), handler.getStackInSlot(ADDITION_SLOT)};
	}

	/**
	 * Vanilla smithing has no template. Ingredient.EMPTY only matches an empty slot.
	 */
	private record RecipeSlots(Ingredient template, Ingredient base, Ingredient addition) {
		private static RecipeSlots of(DireSmithingRecipe recipe) {
			return new RecipeSlots(recipe.getTemplate(), recipe.getBase(), recipe.getAddition());
		}

		private static RecipeSlots of(UpgradeRecipe recipe) {
			return new RecipeSlots(Ingredient.EMPTY, recipe.base, recipe.addition);
		}

		// Empty slots still pass
		private boolean accepts(ItemStack[] inputs) {
			return (inputs[TEMPLATE_SLOT].isEmpty() || template.test(inputs[TEMPLATE_SLOT]))
					&& (inputs[BASE_SLOT].isEmpty() || base.test(inputs[BASE_SLOT]))
					&& (inputs[ADDITION_SLOT].isEmpty() || addition.test(inputs[ADDITION_SLOT]));
		}

		private boolean isComplete(ItemStack[] inputs) {
			return template.test(inputs[TEMPLATE_SLOT]) && base.test(inputs[BASE_SLOT]) && addition.test(inputs[ADDITION_SLOT]);
		}

		private boolean isIngredient(ItemStack stack) {
			return template.test(stack) || base.test(stack) || addition.test(stack);
		}
	}
}
