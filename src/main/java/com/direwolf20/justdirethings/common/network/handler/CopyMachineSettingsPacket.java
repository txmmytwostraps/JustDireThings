package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.MachineSettingsCopier;
import com.direwolf20.justdirethings.common.network.data.CopyMachineSettingsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CopyMachineSettingsPacket {
	public static final CopyMachineSettingsPacket INSTANCE = new CopyMachineSettingsPacket();

	public static CopyMachineSettingsPacket get() {
		return INSTANCE;
	}

	public void handle(final CopyMachineSettingsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			ItemStack itemStack = sender.getMainHandItem();
			if (!(itemStack.getItem() instanceof MachineSettingsCopier))
				itemStack = sender.getOffhandItem();
			if (!(itemStack.getItem() instanceof MachineSettingsCopier))
				return;

			MachineSettingsCopier.setSettings(itemStack, payload.area(), payload.offset(), payload.filter(), payload.redstone());
		});
		ctx.get().setPacketHandled(true);
	}
}
