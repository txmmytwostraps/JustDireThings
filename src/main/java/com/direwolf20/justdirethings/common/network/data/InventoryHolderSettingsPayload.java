package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record InventoryHolderSettingsPayload(
		boolean compareNBT,
		boolean filtersOnly,
		boolean compareCounts,
		boolean automatedFiltersOnly,
		boolean automatedCompareCounts,
		boolean renderPlayer,
		int renderedSlot
) {
	public InventoryHolderSettingsPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(compareNBT);
		buffer.writeBoolean(filtersOnly);
		buffer.writeBoolean(compareCounts);
		buffer.writeBoolean(automatedFiltersOnly);
		buffer.writeBoolean(automatedCompareCounts);
		buffer.writeBoolean(renderPlayer);
		buffer.writeInt(renderedSlot);
	}
}
