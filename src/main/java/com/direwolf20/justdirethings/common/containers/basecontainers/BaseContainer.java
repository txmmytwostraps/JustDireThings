package com.direwolf20.justdirethings.common.containers.basecontainers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public abstract class BaseContainer extends AbstractContainerMenu {
	public BaseContainer(@Nullable MenuType<?> p_i50105_1_, int p_i50105_2_) {
		super(p_i50105_1_, p_i50105_2_);
	}

	protected void addPlayerSlots(Inventory playerInventory, int inX, int inY) {
		// Player inventory
		addSlotBox(new InvWrapper(playerInventory), 9, inX, inY, 9, 18, 3, 18);

		// Hotbar
		inY += 58;
		addSlotRange(new InvWrapper(playerInventory), 0, inX, inY, 9, 18);
	}

	protected void addPlayerSlots(Inventory playerInventory) {
		addPlayerSlots(playerInventory, 8, 84);
	}

	protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
		for (int i = 0; i < amount; i++) {
			addSlot(new SlotItemHandler(handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0; j < verAmount; j++) {
			index = addSlotRange(handler, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	protected ItemStack quickMoveBasicFilter(ItemStack currentStack, int startSlot, int SLOTS) {
		for (int i = startSlot; i < startSlot + SLOTS; i++) { //Prevents the same item from going in there more than once.
			if (ItemStack.isSameItemSameTags(this.slots.get(i).getItem(), currentStack))
				return ItemStack.EMPTY;
		}
		if (!this.moveItemStackTo(currentStack, startSlot, startSlot + SLOTS, false)) {
			return ItemStack.EMPTY;
		}
		return currentStack;
	}

	// Server only, so client slot syncs stay silent
	protected static void onEquipItem(Player pPlayer, EquipmentSlot pSlot, ItemStack pNewItem, ItemStack pOldItem) {
		if (pPlayer.level.isClientSide || pPlayer.isSpectator() || ItemStack.isSameItemSameTags(pOldItem, pNewItem))
			return;
		EquipmentSlot itemSlot = LivingEntity.getEquipmentSlotForItem(pNewItem);
		if (!pPlayer.isSilent() && itemSlot == pSlot) {
			SoundEvent equipSound = pNewItem.getEquipSound();
			// Shields, pumpkins and heads have no sound of their own
			pPlayer.level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), equipSound != null ? equipSound : SoundEvents.ARMOR_EQUIP_GENERIC, pPlayer.getSoundSource(), 1.0F, 1.0F);
		}
		if (pSlot.getType() == EquipmentSlot.Type.ARMOR && itemSlot != EquipmentSlot.MAINHAND)
			pPlayer.gameEvent(GameEvent.EQUIP);
	}
}
