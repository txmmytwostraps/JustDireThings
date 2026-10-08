package com.direwolf20.justdirethings.common.containers.handlers;

import com.direwolf20.justdirethings.common.capabilities.ItemStackItemHandler;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

public class PotionCanisterHandler extends ItemStackItemHandler {
	public PotionCanisterHandler(ItemStack parent, String key, int size) {
		super(parent, key, size);
	}

	@Override
	protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
		if (!newStack.isEmpty() && newStack.getItem() instanceof PotionItem) {
			PotionCanister.attemptFill(parent);
		}
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.getItem() instanceof PotionItem || stack.getItem() instanceof BottleItem || stack.isEmpty();
	}
}
