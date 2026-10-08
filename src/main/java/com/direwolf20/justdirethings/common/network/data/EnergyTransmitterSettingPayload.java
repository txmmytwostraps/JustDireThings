package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record EnergyTransmitterSettingPayload(
		boolean showParticles
) {
	public EnergyTransmitterSettingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(showParticles);
	}
}
