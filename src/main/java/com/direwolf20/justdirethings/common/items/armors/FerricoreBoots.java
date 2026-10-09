package com.direwolf20.justdirethings.common.items.armors;

import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseBoots;
import com.direwolf20.justdirethings.common.items.armors.utils.ArmorTiers;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityParams;
import com.direwolf20.justdirethings.setup.ModSetup;
import net.minecraft.world.item.Item;

public class FerricoreBoots extends BaseBoots {
	public FerricoreBoots() {
		super(ArmorTiers.FERRICORE, new Item.Properties().tab(ModSetup.TAB_JUSTDIRETHINGS));
		registerAbility(Ability.STEPHEIGHT);
		registerAbility(Ability.JUMPBOOST, new AbilityParams(1, 1, 1, 1));
	}
}
