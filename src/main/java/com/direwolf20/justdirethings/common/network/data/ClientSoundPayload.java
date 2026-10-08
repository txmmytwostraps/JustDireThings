package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientSoundPayload(
		ResourceLocation soundEvent,
		float pitch,
		float volume
) {
	public ClientSoundPayload(final FriendlyByteBuf buffer) {
		this(buffer.readResourceLocation(), buffer.readFloat(), buffer.readFloat());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(soundEvent);
		buffer.writeFloat(pitch);
		buffer.writeFloat(volume);
	}
}
