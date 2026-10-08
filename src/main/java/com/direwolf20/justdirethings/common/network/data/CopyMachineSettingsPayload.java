package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record CopyMachineSettingsPayload(
		boolean area, boolean offset,
		boolean filter, boolean redstone
) {
	public CopyMachineSettingsPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(area);
		buffer.writeBoolean(offset);
		buffer.writeBoolean(filter);
		buffer.writeBoolean(redstone);
	}
}
