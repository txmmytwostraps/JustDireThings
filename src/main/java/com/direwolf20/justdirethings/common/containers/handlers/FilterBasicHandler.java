package com.direwolf20.justdirethings.common.containers.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FilterBasicHandler extends ItemStackHandler {
	public FilterBasicHandler(int size) {
		super(size);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		NonNullList<ItemStack> oldStacks = stacks;
		super.deserializeNBT(nbt);
		// Keep unchanged stacks. Open screens compare them by identity.
		for (int i = 0; i < Math.min(oldStacks.size(), stacks.size()); i++) {
			if (ItemStack.matches(oldStacks.get(i), stacks.get(i)))
				stacks.set(i, oldStacks.get(i));
		}
	}
}
