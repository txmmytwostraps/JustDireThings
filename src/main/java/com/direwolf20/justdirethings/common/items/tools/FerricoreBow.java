package com.direwolf20.justdirethings.common.items.tools;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.setup.ModSetup;

public class FerricoreBow extends BaseBow {
	public FerricoreBow() {
		super(new Properties().tab(ModSetup.TAB_JUSTDIRETHINGS).durability(250));
		registerAbility(Ability.POTIONARROW);
	}
}
