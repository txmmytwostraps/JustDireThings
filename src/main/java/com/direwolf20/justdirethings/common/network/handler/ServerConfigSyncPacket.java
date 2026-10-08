package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.network.data.ServerConfigSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerConfigSyncPacket {
	public static final ServerConfigSyncPacket INSTANCE = new ServerConfigSyncPacket();

	public static ServerConfigSyncPacket get() {
		return INSTANCE;
	}

	public void handle(final ServerConfigSyncPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			// Singleplayer already shares the server's config
			if (Minecraft.getInstance().isLocalServer())
				return;
			// Only ours. Other configs would stop saving.
			Optional.ofNullable(ConfigTracker.INSTANCE.fileMap().get(payload.fileName()))
					.filter(config -> config.getType() == ModConfig.Type.SERVER && config.getModId().equals(JustDireThings.MODID))
					.ifPresent(config -> config.acceptSyncedConfig(payload.fileData()));
		}));
		ctx.get().setPacketHandled(true);
	}
}
