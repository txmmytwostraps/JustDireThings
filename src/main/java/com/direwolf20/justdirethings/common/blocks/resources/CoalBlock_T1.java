package com.direwolf20.justdirethings.common.blocks.resources;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CoalBlock_T1 extends Block {
	public CoalBlock_T1() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.requiresCorrectToolForDrops()
				.strength(5.0F, 6.0F)
		);
	}

	public int getBurnSpeedMultiplier() {
		return 2;
	}
}
