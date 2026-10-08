package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ToggleToolLeftRightClickPayload(
		int slot,
		String abilityName,
		int button,
		int keyCode,
		boolean isMouse,
		boolean requireEquipped
) {
	public ToggleToolLeftRightClickPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
		buffer.writeUtf(abilityName);
		buffer.writeInt(button);
		buffer.writeInt(keyCode);
		buffer.writeBoolean(isMouse);
		buffer.writeBoolean(requireEquipped);
	}
}
