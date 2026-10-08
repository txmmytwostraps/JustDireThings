package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ExperienceHolderPayload(
		boolean add,
		int levels
) {
	public ExperienceHolderPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(add);
		buffer.writeInt(levels);
	}
}
