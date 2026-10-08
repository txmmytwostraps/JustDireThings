package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record InventoryHolderSaveSlotPayload(
		int slot
) {
	public InventoryHolderSaveSlotPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
	}
}
