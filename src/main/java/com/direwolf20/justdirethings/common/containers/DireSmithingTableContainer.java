package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.blockentities.DireSmithingTableBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.datagen.recipes.DireSmithingRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import java.util.OptionalInt;
import java.util.stream.IntStream;

public class DireSmithingTableContainer extends BaseMachineContainer {
	private static final int INVENTORY_START = DireSmithingTableBE.OUTPUT_SLOT + 1;
	private static final int INVENTORY_END = INVENTORY_START + 27;
	private static final int USE_ROW_END = INVENTORY_END + 9;

	public DireSmithingTableContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
		this(windowId, playerInventory, extraData.readBlockPos());
	}

	public DireSmithingTableContainer(int windowId, Inventory playerInventory, BlockPos blockPos) {
		super(Registration.DireSmithingTable_Container.get(), windowId, playerInventory, blockPos);
		addPlayerSlots(player.getInventory());
	}

	// Same layout as the vanilla smithing menu
	@Override
	public void addMachineSlots() {
		machineHandler = baseMachineBE.getMachineHandler();
		addSlotRange(machineHandler, DireSmithingTableBE.TEMPLATE_SLOT, 8, 48, 3, 18);
		addSlotRange(machineHandler, DireSmithingTableBE.OUTPUT_SLOT, 98, 48, 1, 18);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(ContainerLevelAccess.create(player.level, pos), player, Registration.DireSmithingTable.get());
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.index != DireSmithingTableBE.OUTPUT_SLOT && super.canTakeItemForPickAll(stack, slot);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack currentStack = slot.getItem();
			itemstack = currentStack.copy();
			if (index <= DireSmithingTableBE.OUTPUT_SLOT) { //Table Slots to Player Inventory, output fills from the hotbar
				if (!this.moveItemStackTo(currentStack, INVENTORY_START, USE_ROW_END, index == DireSmithingTableBE.OUTPUT_SLOT))
					return ItemStack.EMPTY;
			} else {
				OptionalInt inputSlot = findSlotToQuickMoveTo(currentStack);
				if (inputSlot.isPresent()) {
					if (!this.moveItemStackTo(currentStack, inputSlot.getAsInt(), DireSmithingTableBE.OUTPUT_SLOT, false))
						return ItemStack.EMPTY;
				} else if (index < INVENTORY_END) {
					if (!this.moveItemStackTo(currentStack, INVENTORY_END, USE_ROW_END, false))
						return ItemStack.EMPTY;
				} else if (!this.moveItemStackTo(currentStack, INVENTORY_START, INVENTORY_END, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (currentStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (currentStack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, currentStack);
		}
		return itemstack;
	}

	// Our own recipes first, then vanilla smithing
	private OptionalInt findSlotToQuickMoveTo(ItemStack stack) {
		if (!(baseMachineBE instanceof DireSmithingTableBE direSmithingTableBE))
			return OptionalInt.empty();
		IntStream recipeSlots = direSmithingTableBE.getRecipes().stream().flatMapToInt(recipe -> findSlotMatchingIngredient(recipe, stack).stream());
		IntStream upgradeRecipeSlots = direSmithingTableBE.getUpgradeRecipes().stream().flatMapToInt(recipe -> findSlotMatchingIngredient(recipe, stack).stream());
		return IntStream.concat(recipeSlots, upgradeRecipeSlots)
				.filter(slot -> !getSlot(slot).hasItem())
				.findFirst();
	}

	private static OptionalInt findSlotMatchingIngredient(DireSmithingRecipe recipe, ItemStack stack) {
		if (recipe.isTemplateIngredient(stack))
			return OptionalInt.of(DireSmithingTableBE.TEMPLATE_SLOT);
		if (recipe.isBaseIngredient(stack))
			return OptionalInt.of(DireSmithingTableBE.BASE_SLOT);
		if (recipe.isAdditionIngredient(stack))
			return OptionalInt.of(DireSmithingTableBE.ADDITION_SLOT);
		return OptionalInt.empty();
	}

	// Vanilla smithing has no template
	private static OptionalInt findSlotMatchingIngredient(UpgradeRecipe recipe, ItemStack stack) {
		if (recipe.base.test(stack))
			return OptionalInt.of(DireSmithingTableBE.BASE_SLOT);
		if (recipe.isAdditionIngredient(stack))
			return OptionalInt.of(DireSmithingTableBE.ADDITION_SLOT);
		return OptionalInt.empty();
	}
}
