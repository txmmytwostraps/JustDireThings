package com.direwolf20.justdirethings.common.items.armors;

import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseLeggings;
import com.direwolf20.justdirethings.common.items.armors.utils.ArmorTiers;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityParams;
import com.direwolf20.justdirethings.setup.ModSetup;

public class FerricoreLeggings extends BaseLeggings {
	public FerricoreLeggings() {
		super(ArmorTiers.FERRICORE, new Properties().tab(ModSetup.TAB_JUSTDIRETHINGS));
		registerAbility(Ability.RUNSPEED, new AbilityParams(1, 1, 1));
		registerAbility(Ability.WALKSPEED, new AbilityParams(1, 1, 1));
	}
}
