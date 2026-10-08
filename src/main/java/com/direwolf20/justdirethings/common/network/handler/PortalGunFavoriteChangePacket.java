package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoriteChangePayload;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunFavoriteChangePacket {
	public static final PortalGunFavoriteChangePacket INSTANCE = new PortalGunFavoriteChangePacket();

	public static PortalGunFavoriteChangePacket get() {
		return INSTANCE;
	}

	public void handle(final PortalGunFavoriteChangePayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			ItemStack itemStack = sender.getMainHandItem();
			if (!(itemStack.getItem() instanceof PortalGunV2))
				itemStack = sender.getOffhandItem();
			if (!(itemStack.getItem() instanceof PortalGunV2))
				return;

			Level level = sender.level;
			if (!payload.add())
				PortalGunV2.removeFavorite(itemStack, payload.favorite());
			else {
				NBTHelpers.PortalDestination portalDestination = PortalGunV2.getFavorite(itemStack, payload.favorite());
				if (!payload.editing()) {
					Vec3 position = sender.position();
					Direction facing = MiscHelpers.getFacingDirection(sender);
					portalDestination = new NBTHelpers.PortalDestination(new NBTHelpers.GlobalVec3(level.dimension(), position), facing, payload.name());
					PortalGunV2.addFavorite(itemStack, payload.favorite(), portalDestination);
				} else {
					Vec3 position = payload.coordinates().equals(Vec3.ZERO) ? sender.position() : payload.coordinates();
					Direction facing = portalDestination == null || portalDestination.equals(NBTHelpers.PortalDestination.EMPTY) ? MiscHelpers.getFacingDirection(sender) : portalDestination.direction();
					ResourceKey<Level> dimension = portalDestination == null || portalDestination.equals(NBTHelpers.PortalDestination.EMPTY) ? level.dimension() : portalDestination.globalVec3().dimension();
					NBTHelpers.PortalDestination newDestination = new NBTHelpers.PortalDestination(new NBTHelpers.GlobalVec3(dimension, position), facing, payload.name());
					PortalGunV2.addFavorite(itemStack, payload.favorite(), newDestination);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
