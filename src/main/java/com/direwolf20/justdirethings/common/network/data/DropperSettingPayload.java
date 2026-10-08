package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record DropperSettingPayload(
		int dropCount,
		int pickupDelay
) {
	public DropperSettingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(dropCount);
		buffer.writeInt(pickupDelay);
	}
}
