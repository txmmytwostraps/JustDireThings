package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.ParadoxRenderPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParadoxRenderPacket {
	public static final ParadoxRenderPacket INSTANCE = new ParadoxRenderPacket();

	public static ParadoxRenderPacket get() {
		return INSTANCE;
	}

	public void handle(final ParadoxRenderPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			AbstractContainerMenu container = sender.containerMenu;

			if (container instanceof BaseMachineContainer baseMachineContainer && baseMachineContainer.baseMachineBE instanceof ParadoxMachineBE paradoxMachineBE) {
				paradoxMachineBE.setRenderParadox(payload.renderParadox(), payload.targetType());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
