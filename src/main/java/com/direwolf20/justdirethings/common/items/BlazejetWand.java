package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityParams;
import com.direwolf20.justdirethings.common.items.interfaces.BaseToggleableTool;
import com.direwolf20.justdirethings.common.items.interfaces.LeftClickableTool;
import com.direwolf20.justdirethings.setup.ModSetup;

public class BlazejetWand extends BaseToggleableTool implements LeftClickableTool {
	public BlazejetWand() {
		super(new Properties()
				.tab(ModSetup.TAB_JUSTDIRETHINGS)
				.fireResistant()
				.durability(200));
		registerAbility(Ability.LAVAREPAIR);
		registerAbility(Ability.AIRBURST, new AbilityParams(1, 2, 1, 2));
	}
}
