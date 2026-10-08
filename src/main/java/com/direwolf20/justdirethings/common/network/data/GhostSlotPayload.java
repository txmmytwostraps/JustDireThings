package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record GhostSlotPayload(
		int slotNumber,
		ItemStack stack,
		int count,
		int mbAmt
) {
	public GhostSlotPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readItem(), buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slotNumber);
		buffer.writeItemStack(stack, false); //Full tag, since the server keeps this as a filter
		buffer.writeInt(count);
		buffer.writeInt(mbAmt);
	}
}
