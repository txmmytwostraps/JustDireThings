package com.direwolf20.justdirethings.common.containers.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * The Pocket Generator hands out copies, so changes made in place are written back on {@link #setChanged()}.
 */
public class FuelSlot extends SlotItemHandler {
	private ItemStack returnedStack;

	public FuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
	}

	@Override
	@Nonnull
	public ItemStack getItem() {
		returnedStack = super.getItem();
		return returnedStack;
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
		((IItemHandlerModifiable) getItemHandler()).setStackInSlot(getSlotIndex(), stack);
		returnedStack = stack;
	}

	@Override
	public void setChanged() {
		if (returnedStack != null)
			set(returnedStack);
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		ItemStack stack = super.getItem().copy();
		ItemStack removed = stack.split(amount);
		set(stack);
		returnedStack = null;
		return removed;
	}
}
