package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.network.data.ToolSettingsGUIPayload;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToolSettingsGUIPacket {
	public static final ToolSettingsGUIPacket INSTANCE = new ToolSettingsGUIPacket();

	public static ToolSettingsGUIPacket get() {
		return INSTANCE;
	}

	public void handle(final ToolSettingsGUIPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			sender.openMenu(new SimpleMenuProvider(
					(windowId, playerInventory, playerEntity) -> new ToolSettingContainer(windowId, playerInventory, sender), new TranslatableComponent("")));
		});
		ctx.get().setPacketHandled(true);
	}
}
