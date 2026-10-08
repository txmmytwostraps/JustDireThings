package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ServerConfigSyncPayload(
		String fileName,
		byte[] fileData
) {
	public ServerConfigSyncPayload(final FriendlyByteBuf buffer) {
		this(buffer.readUtf(), buffer.readByteArray());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(fileName);
		buffer.writeByteArray(fileData);
	}
}
