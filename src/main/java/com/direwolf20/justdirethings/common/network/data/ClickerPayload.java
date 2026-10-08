package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ClickerPayload(
		int clickType,
		int clickTarget,
		boolean sneaking,
		boolean showFakePlayer,
		int maxHoldTicks
) {
	public ClickerPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(clickType);
		buffer.writeInt(clickTarget);
		buffer.writeBoolean(sneaking);
		buffer.writeBoolean(showFakePlayer);
		buffer.writeInt(maxHoldTicks);
	}
}
