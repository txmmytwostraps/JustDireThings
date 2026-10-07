package com.direwolf20.justdirethings.common.fluids.timefluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;

public class TimeFluidAttributes extends JustDireFluidAttributes {
	public static final int TINT_COLOR = 0x3300FF00;
	private static final int WORLD_TINT_COLOR = 0x7700FF00;

	protected TimeFluidAttributes(Builder builder, Fluid fluid) {
		super(builder, fluid);
	}

	@Override
	public int getColor(BlockAndTintGetter level, BlockPos pos) {
		return WORLD_TINT_COLOR;
	}
}
