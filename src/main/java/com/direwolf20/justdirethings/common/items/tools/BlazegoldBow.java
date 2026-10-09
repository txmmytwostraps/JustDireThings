package com.direwolf20.justdirethings.common.items.tools;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.Helpers;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.setup.ModSetup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class BlazegoldBow extends BaseBow {
	public BlazegoldBow() {
		super(new Properties().tab(ModSetup.TAB_JUSTDIRETHINGS).durability(450).fireResistant());
		registerAbility(Ability.POTIONARROW);
		registerAbility(Ability.SPLASH);
		registerAbility(Ability.LAVAREPAIR);
	}

	@Override
	public float getMaxDraw() {
		return 17;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if (canUseAbility(stack, Ability.LAVAREPAIR))
			return Helpers.doLavaRepair(stack, entity);
		return false;
	}
}
