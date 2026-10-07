package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.util.ItemStackKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import javax.annotation.Nonnull;

public class InventoryHolderItemHandler extends ItemStackHandler {
	private final InventoryHolderBE be;
	private final ItemStackHandler delegate;

	public InventoryHolderItemHandler(InventoryHolderBE be, ItemStackHandler delegate) {
		super(delegate.getSlots());
		this.be = be;
		this.delegate = delegate;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (be == null)
			return ItemStack.EMPTY;
		int allowedAmt = be.allowedExtractAmount(slot, amount);
		return delegate.extractItem(slot, allowedAmt, simulate);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStackKey key = new ItemStackKey(stack, be.compareNBT);
		if (be.filteredCache.containsKey(key)) {
			List<Integer> slotsList = be.filteredCache.get(key);
			if (slotsList.contains(slot)) //Skip this if we're already inserting into a filtered slot
				return insertItemProxy(slot, stack, simulate);
			for (Integer i : slotsList) {
				stack = insertItemProxy(i, stack, simulate);
			}
		}
		return insertItemProxy(slot, stack, simulate);
	}

	@Nonnull
	public ItemStack insertItemProxy(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		if (!isItemValid(slot, stack))
			return stack;
		validateSlotIndex(slot);
		ItemStack existing = delegate.getStackInSlot(slot);
		int limit = getStackLimit(slot, stack);
		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;
			limit -= existing.getCount();
		}
		if (limit <= 0)
			return stack;
		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				delegate.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			onContentsChanged(slot);
		}
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (be == null)
			return false;
		if (be.automatedFiltersOnly)
			return be.isStackValidFilter(stack, slot);
		return delegate.isItemValid(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		if (be == null)
			return 0;
		int limit = be.getSlotLimit(slot);
		return limit == -1 ? Item.MAX_STACK_SIZE : limit;
	}

	@Override
	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		if (be == null)
			return 0;
		int limit = be.getSlotLimit(slot);
		if (limit == -1)
			return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		return Math.min(limit, stack.getMaxStackSize());
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
}
