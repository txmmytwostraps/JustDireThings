package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import net.minecraft.world.item.ItemStack;

public class EnergyStorageItemStackNoReceive extends EnergyStorageItemstack {
	public EnergyStorageItemStackNoReceive(ItemStack itemStack, PoweredItem poweredItem) {
		super(itemStack, poweredItem);
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	/**
	 * Pocket Generators need to be able to power themselves, but not receive energy from anything else. This is used to do this.
	 */
	public int forceReceiveEnergy(int maxReceive, boolean simulate) {
		int energy = getEnergyStored();
		int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(getMaxEnergyStored(), maxReceive));
		if (!simulate)
			setEnergy(energy + energyReceived);
		return energyReceived;
	}
}
