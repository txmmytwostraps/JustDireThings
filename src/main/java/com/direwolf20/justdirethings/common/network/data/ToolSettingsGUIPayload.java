package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record ToolSettingsGUIPayload(

) {
	public ToolSettingsGUIPayload(final FriendlyByteBuf buffer) {
		this();
	}

	public void write(FriendlyByteBuf buffer) {
	}
}
