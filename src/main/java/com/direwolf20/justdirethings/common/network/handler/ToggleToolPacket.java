package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.network.data.ToggleToolPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleToolPacket {
	public static final ToggleToolPacket INSTANCE = new ToggleToolPacket();

	public static ToggleToolPacket get() {
		return INSTANCE;
	}

	public void handle(final ToggleToolPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;

			ItemStack toggleableItem = ToggleableItem.getToggleableItem(sender);
			if (toggleableItem.getItem() instanceof ToggleableItem toggleableTool) {
				toggleableTool.toggleEnabled(toggleableItem, sender);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
