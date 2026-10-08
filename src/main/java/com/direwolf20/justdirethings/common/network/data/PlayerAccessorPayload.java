package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record PlayerAccessorPayload(
		int direction,
		int accessType
) {
	public PlayerAccessorPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(direction);
		buffer.writeInt(accessType);
	}
}
