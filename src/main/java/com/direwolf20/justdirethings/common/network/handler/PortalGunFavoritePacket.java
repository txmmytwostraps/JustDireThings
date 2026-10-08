package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoritePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunFavoritePacket {
	public static final PortalGunFavoritePacket INSTANCE = new PortalGunFavoritePacket();

	public static PortalGunFavoritePacket get() {
		return INSTANCE;
	}

	public void handle(final PortalGunFavoritePayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			ItemStack itemStack = sender.getMainHandItem();
			if (!(itemStack.getItem() instanceof PortalGunV2))
				itemStack = sender.getOffhandItem();
			if (!(itemStack.getItem() instanceof PortalGunV2))
				return;

			PortalGunV2.setFavoritePosition(itemStack, payload.favorite());
			PortalGunV2.setStayOpen(itemStack, payload.staysOpen());
		});
		ctx.get().setPacketHandled(true);
	}
}
