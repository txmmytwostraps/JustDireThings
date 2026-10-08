package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record SensorPayload(
		int senseTarget,
		boolean strongSignal,
		int senseCount,
		int equality
) {
	public SensorPayload(final FriendlyByteBuf buffer) {
		this(buffer.readVarInt(), buffer.readBoolean(), buffer.readVarInt(), buffer.readVarInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(senseTarget);
		buffer.writeBoolean(strongSignal);
		buffer.writeVarInt(senseCount);
		buffer.writeVarInt(equality);
	}
}
