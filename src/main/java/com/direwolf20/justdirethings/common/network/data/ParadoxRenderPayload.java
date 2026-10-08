package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ParadoxRenderPayload(
		boolean renderParadox,
		int targetType
) {
	public ParadoxRenderPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(renderParadox);
		buffer.writeInt(targetType);
	}
}
