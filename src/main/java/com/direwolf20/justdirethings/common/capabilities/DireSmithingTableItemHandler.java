package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.blockentities.DireSmithingTableBE;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * What automation sees. The table sorts inputs, and only the output comes out.
 */
public class DireSmithingTableItemHandler implements IItemHandler {
	private final DireSmithingTableBE be;
	private final ItemStackHandler delegate;

	public DireSmithingTableItemHandler(DireSmithingTableBE be, ItemStackHandler delegate) {
		this.be = be;
		this.delegate = delegate;
	}

	@Override
	public int getSlots() {
		return delegate.getSlots();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return delegate.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (slot == DireSmithingTableBE.OUTPUT_SLOT)
			return stack;
		return be.insertSorted(stack, simulate);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot != DireSmithingTableBE.OUTPUT_SLOT)
			return ItemStack.EMPTY;
		return delegate.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return delegate.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return slot != DireSmithingTableBE.OUTPUT_SLOT && be.isRecipeIngredient(stack);
	}
}
