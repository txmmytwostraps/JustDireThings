package com.direwolf20.justdirethings.common.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;

/**
 * Curios is optional, so only this class touches its API. Check ModList before calling in.
 */
public final class CuriosIntegration {
	private CuriosIntegration() {
	}

	@Nullable
	public static IItemHandler getEquippedCurios(LivingEntity livingEntity) {
		return CuriosApi.getCuriosHelper().getEquippedCurios(livingEntity).orElse(null);
	}
}
