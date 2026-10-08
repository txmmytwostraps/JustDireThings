package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.InventoryHolderContainer;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderMoveItemsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventoryHolderMoveItemsPacket {
	public static final InventoryHolderMoveItemsPacket INSTANCE = new InventoryHolderMoveItemsPacket();

	public static InventoryHolderMoveItemsPacket get() {
		return INSTANCE;
	}

	public void handle(final InventoryHolderMoveItemsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			AbstractContainerMenu container = sender.containerMenu;

			if (container instanceof InventoryHolderContainer inventoryHolderContainer) {
				int moveType = payload.moveType();
				if (moveType == 0)
					inventoryHolderContainer.sendAllItemsToMachine();
				else if (moveType == 1)
					inventoryHolderContainer.sendAllItemsToPlayer();
				else if (moveType == 2)
					inventoryHolderContainer.swapItems();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
