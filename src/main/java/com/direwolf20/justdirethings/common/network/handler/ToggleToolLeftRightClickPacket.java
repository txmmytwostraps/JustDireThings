package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.LeftClickableTool;
import com.direwolf20.justdirethings.common.items.interfaces.ToolRecords;
import com.direwolf20.justdirethings.common.network.data.ToggleToolLeftRightClickPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Locale;
import java.util.function.Supplier;

public class ToggleToolLeftRightClickPacket {
	public static final ToggleToolLeftRightClickPacket INSTANCE = new ToggleToolLeftRightClickPacket();

	public static ToggleToolLeftRightClickPacket get() {
		return INSTANCE;
	}

	public void handle(final ToggleToolLeftRightClickPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;

			ItemStack stack = sender.getInventory().getItem(payload.slot());
			if (stack.getItem() instanceof LeftClickableTool) {
				Ability ability = Ability.valueOf(payload.abilityName().toUpperCase(Locale.ROOT));
				LeftClickableTool.setBindingMode(stack, ability, payload.button());
				if (payload.button() == 0) //Right Click
					LeftClickableTool.removeFromLeftClickList(stack, ability);
				else if (payload.button() == 1) //Left Click
					LeftClickableTool.addToLeftClickList(stack, ability);
				else if (payload.button() == 2) { //Custom Keybind
					if (payload.keyCode() == -1)
						LeftClickableTool.removeFromCustomBindingList(stack, ability);
					else
						LeftClickableTool.addToCustomBindingList(stack, new ToolRecords.AbilityBinding(payload.abilityName(), payload.keyCode(), payload.isMouse(), payload.requireEquipped()));
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
