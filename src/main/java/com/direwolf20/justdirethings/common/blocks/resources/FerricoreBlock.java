package com.direwolf20.justdirethings.common.blocks.resources;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class FerricoreBlock extends Block {
	public FerricoreBlock() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.sound(SoundType.METAL)
				.requiresCorrectToolForDrops()
				.strength(5.0F, 6.0F)
		);
	}
}
