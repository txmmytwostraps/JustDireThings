package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ItemCollectorSettingsPayload(
		boolean respectPickupDelay,
		boolean showParticles
) {
	public ItemCollectorSettingsPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(respectPickupDelay);
		buffer.writeBoolean(showParticles);
	}
}
