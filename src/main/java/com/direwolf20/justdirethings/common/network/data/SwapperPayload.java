package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record SwapperPayload(
		boolean swapBlocks,
		int swap_entity_type
) {
	public SwapperPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(swapBlocks);
		buffer.writeInt(swap_entity_type);
	}
}
