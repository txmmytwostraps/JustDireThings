package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record AreaAffectingPayload(
		double xRadius, double yRadius, double zRadius,
		int xOffset, int yOffset, int zOffset,
		boolean renderArea
) {
	public AreaAffectingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeDouble(xRadius);
		buffer.writeDouble(yRadius);
		buffer.writeDouble(zRadius);
		buffer.writeInt(xOffset);
		buffer.writeInt(yOffset);
		buffer.writeInt(zOffset);
		buffer.writeBoolean(renderArea);
	}
}
