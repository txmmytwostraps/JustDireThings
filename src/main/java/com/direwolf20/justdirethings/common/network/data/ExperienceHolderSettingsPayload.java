package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ExperienceHolderSettingsPayload(
		int targetExp,
		boolean ownerOnly,
		boolean collectExp,
		boolean showParticles
) {
	public ExperienceHolderSettingsPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(targetExp);
		buffer.writeBoolean(ownerOnly);
		buffer.writeBoolean(collectExp);
		buffer.writeBoolean(showParticles);
	}
}
