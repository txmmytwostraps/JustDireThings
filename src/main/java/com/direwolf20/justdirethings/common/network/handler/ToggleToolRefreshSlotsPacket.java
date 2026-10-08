package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.network.data.ToggleToolRefreshSlots;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleToolRefreshSlotsPacket {
	public static final ToggleToolRefreshSlotsPacket INSTANCE = new ToggleToolRefreshSlotsPacket();

	public static ToggleToolRefreshSlotsPacket get() {
		return INSTANCE;
	}

	public void handle(final ToggleToolRefreshSlots payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;

			ItemStack stack = sender.getInventory().getItem(payload.slot());
			if (sender.containerMenu instanceof ToolSettingContainer container) {
				container.refreshSlots(stack);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
