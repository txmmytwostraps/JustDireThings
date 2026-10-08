package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record BlockStateFilterPayload(
		int slot,
		CompoundTag compoundTag
) {
	public BlockStateFilterPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readNbt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
		buffer.writeNbt(compoundTag);
	}
}
