package com.direwolf20.justdirethings.common.items.armors;

import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseHelmet;
import com.direwolf20.justdirethings.common.items.armors.utils.ArmorTiers;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.setup.ModSetup;

public class FerricoreHelmet extends BaseHelmet {
	public FerricoreHelmet() {
		super(ArmorTiers.FERRICORE, new Properties().tab(ModSetup.TAB_JUSTDIRETHINGS));
		registerAbility(Ability.MINDFOG);
	}
}
