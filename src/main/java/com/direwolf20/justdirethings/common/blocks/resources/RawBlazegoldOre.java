package com.direwolf20.justdirethings.common.blocks.resources;

import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseRawOre;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class RawBlazegoldOre extends BaseRawOre {
	public RawBlazegoldOre() {
		super(Properties.of(Material.CLAY, MaterialColor.NONE)
				.sound(SoundType.AMETHYST)
				.requiresCorrectToolForDrops()
				.noOcclusion()
				.strength(5.0F, 6.0F)
		);
	}
}
