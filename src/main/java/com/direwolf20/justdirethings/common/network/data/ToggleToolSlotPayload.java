package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ToggleToolSlotPayload(
		String settingName,
		int slot,
		int typeTool,
		int value
) {
	public ToggleToolSlotPayload(final FriendlyByteBuf buffer) {
		this(buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(settingName);
		buffer.writeInt(slot);
		buffer.writeInt(typeTool);
		buffer.writeInt(value);
	}
}
