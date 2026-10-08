package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record FilterSettingPayload(
		boolean allowList,
		boolean compareNBT,
		int blockItemFilter
) {
	public FilterSettingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(allowList);
		buffer.writeBoolean(compareNBT);
		buffer.writeInt(blockItemFilter);
	}
}
