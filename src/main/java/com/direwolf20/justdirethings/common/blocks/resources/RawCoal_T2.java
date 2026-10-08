package com.direwolf20.justdirethings.common.blocks.resources;

import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseRawOre;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class RawCoal_T2 extends BaseRawOre {
	public RawCoal_T2() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.requiresCorrectToolForDrops()
				.noOcclusion()
				.strength(5.0F, 6.0F)
		);
	}
}
