package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Kept out of the mixin package so it can be referenced at runtime.
 */
public final class NightVisionHelper {
	/**
	 * Only set during the light texture update, so fog color is left alone.
	 */
	@Nullable
	public static Player lightTexturePlayer;

	private NightVisionHelper() {
	}

	public static boolean hasNightVision(LivingEntity livingEntity) {
		if (livingEntity != lightTexturePlayer)
			return false;
		ItemStack helmet = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
		return helmet.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(helmet, Ability.NIGHTVISION);
	}
}
