package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.direwolf20.justdirethings.common.network.data.ParadoxSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParadoxSyncPacket {
	public static final ParadoxSyncPacket INSTANCE = new ParadoxSyncPacket();

	public static ParadoxSyncPacket get() {
		return INSTANCE;
	}

	public void handle(final ParadoxSyncPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			//Not Minecraft.level, which loads ClientLevel on a dedicated server
			Level level = Minecraft.getInstance().player.level;
			if (level == null) return;
			BlockEntity blockEntity = level.getBlockEntity(payload.blockPos());
			if (blockEntity instanceof ParadoxMachineBE paradoxMachineBE) {
				paradoxMachineBE.receiveRunTime(payload.runtime());
			}
		}));
		ctx.get().setPacketHandled(true);
	}
}
