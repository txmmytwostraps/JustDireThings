package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ParadoxSyncPayload(
		BlockPos blockPos,
		int runtime
) {
	public ParadoxSyncPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBlockPos(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(blockPos);
		buffer.writeInt(runtime);
	}
}
