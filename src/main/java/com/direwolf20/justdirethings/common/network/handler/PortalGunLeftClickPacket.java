package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGun;
import com.direwolf20.justdirethings.common.network.data.PortalGunLeftClickPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunLeftClickPacket {
	public static final PortalGunLeftClickPacket INSTANCE = new PortalGunLeftClickPacket();

	public static PortalGunLeftClickPacket get() {
		return INSTANCE;
	}

	public void handle(final PortalGunLeftClickPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			ItemStack itemStack = sender.getMainHandItem();
			if (!(itemStack.getItem() instanceof PortalGun))
				itemStack = sender.getOffhandItem();
			if (!(itemStack.getItem() instanceof PortalGun))
				return;

			PortalGun.spawnProjectile(sender.level, sender, itemStack, true);
		});
		ctx.get().setPacketHandled(true);
	}
}
