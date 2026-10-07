package com.direwolf20.justdirethings.common.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Reads and writes the stack's NBT on every call, so all handlers for one stack agree.
 */
public class ItemStackItemHandler implements IItemHandlerModifiable {
	protected final ItemStack parent;
	protected final String key;
	protected final int size;

	public ItemStackItemHandler(ItemStack parent, String key, int size) {
		this.parent = parent;
		this.key = key;
		this.size = size;
	}

	@Override
	public int getSlots() {
		return size;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return getContents().get(slot);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		if (!isItemValid(slot, stack))
			throw new RuntimeException("Invalid stack " + stack + " for slot " + slot);
		NonNullList<ItemStack> contents = getContents();
		if (!ItemStack.matches(contents.get(slot), stack))
			updateContents(contents, stack, slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		validateSlotIndex(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		if (!isItemValid(slot, stack))
			return stack;
		NonNullList<ItemStack> contents = getContents();
		ItemStack existing = contents.get(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;
			limit -= existing.getCount();
		}
		if (limit <= 0)
			return stack;
		int toInsert = Math.min(limit, stack.getCount());
		if (!simulate)
			updateContents(contents, ItemHandlerHelper.copyStackWithSize(stack, existing.getCount() + toInsert), slot);
		return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toInsert);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		validateSlotIndex(slot);
		if (amount == 0)
			return ItemStack.EMPTY;
		NonNullList<ItemStack> contents = getContents();
		ItemStack existing = contents.get(slot);
		if (existing.isEmpty())
			return ItemStack.EMPTY;
		int toExtract = Math.min(amount, existing.getCount());
		if (!simulate)
			updateContents(contents, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract), slot);
		return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}

	protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
	}

	// Fits every stored slot, so a smaller handler drops nothing
	protected NonNullList<ItemStack> getContents() {
		CompoundTag tag = parent.getTagElement(key);
		if (tag == null)
			return NonNullList.withSize(size, ItemStack.EMPTY);
		ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
		int slots = size;
		for (int i = 0; i < items.size(); i++)
			slots = Math.max(slots, (items.getCompound(i).getByte("Slot") & 255) + 1);
		NonNullList<ItemStack> contents = NonNullList.withSize(slots, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, contents);
		return contents;
	}

	protected void updateContents(NonNullList<ItemStack> contents, ItemStack stack, int slot) {
		ItemStack oldStack = contents.get(slot);
		contents.set(slot, stack);
		parent.addTagElement(key, ContainerHelper.saveAllItems(new CompoundTag(), contents));
		onContentsChanged(slot, oldStack, stack);
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= size)
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + size + ")");
	}
}
