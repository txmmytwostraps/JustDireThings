package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record LeftClickPayload(
		int clickType, //0 for empty, 1 for block
		boolean mainHand,
		BlockPos blockPos,
		int direction,
		int inventorySlot,
		int keyCode, //-1 for left click
		boolean isMouse
) {
	public LeftClickPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readBoolean(), buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(clickType);
		buffer.writeBoolean(mainHand);
		buffer.writeBlockPos(blockPos);
		buffer.writeInt(direction);
		buffer.writeInt(inventorySlot);
		buffer.writeInt(keyCode);
		buffer.writeBoolean(isMouse);
	}
}
