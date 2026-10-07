package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Energy and capacity are read from the stack on every call.
 */
public class EnergyStorageItemstack implements IEnergyStorage {
	private static final String NBT_KEY = "Energy";
	protected final ItemStack itemStack;
	private final PoweredItem poweredItem;

	public EnergyStorageItemstack(ItemStack itemStack, PoweredItem poweredItem) {
		this.itemStack = itemStack;
		this.poweredItem = poweredItem;
	}

	protected void setEnergy(int energy) {
		itemStack.getOrCreateTag().putInt(NBT_KEY, energy);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		int energy = getEnergyStored();
		int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(getMaxEnergyStored(), maxReceive));
		if (!simulate)
			setEnergy(energy + energyReceived);
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

		int energy = getEnergyStored();
		int energyExtracted = Math.min(energy, maxExtract);
		if (!simulate)
			setEnergy(energy - energyExtracted);
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		CompoundTag tag = itemStack.getTag();
		return tag == null ? 0 : Math.min(tag.getInt(NBT_KEY), getMaxEnergyStored());
	}

	@Override
	public int getMaxEnergyStored() {
		return poweredItem.getMaxEnergy();
	}

	@Override
	public boolean canExtract() {
		return getMaxEnergyStored() > 0;
	}

	@Override
	public boolean canReceive() {
		return getMaxEnergyStored() > 0;
	}
}
