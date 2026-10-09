package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getSetting;
import static com.direwolf20.justdirethings.util.NBTHelpers.setBoolean;

public interface ToggleableItem {
	default boolean getEnabled(ItemStack stack) {
		return getSetting(stack, "enabled");
	}

	default void toggleEnabled(ItemStack stack, Player player) {
		setBoolean(stack, "enabled", !getSetting(stack, "enabled"));
		boolean nowEnabled = getSetting(stack, "enabled");
		player.displayClientMessage(new TranslatableComponent("justdirethings.toolenabled", stack.getDisplayName(), nowEnabled ? new TranslatableComponent("justdirethings.enabled") : new TranslatableComponent("justdirethings.disabled")), true);
		if (nowEnabled)
			player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
		else
			player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);
	}

	static ItemStack getToggleableItem(Player player) {
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getItem() instanceof ToggleableItem)
			return mainHand;
		ItemStack offHand = player.getOffhandItem();
		if (offHand.getItem() instanceof ToggleableItem)
			return offHand;
		return ItemStack.EMPTY;
	}
}
