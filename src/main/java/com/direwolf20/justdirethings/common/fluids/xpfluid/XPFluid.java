package com.direwolf20.justdirethings.common.fluids.xpfluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class XPFluid extends ForgeFlowingFluid {

	private static ForgeFlowingFluid.Properties makeProperties() {
		return new ForgeFlowingFluid.Properties(() -> Registration.XP_FLUID_SOURCE.get(),
				() -> Registration.XP_FLUID_FLOWING.get(),
				JustDireFluidAttributes.builder(0xFF32CD32).translationKey("fluid_type.justdirethings.xp_fluid_type")
						.density(1000).viscosity(1000).temperature(300))
				.bucket(() -> Registration.XP_FLUID_BUCKET.get()).block(() -> Registration.XP_FLUID_BLOCK.get());
	}

	protected XPFluid() {
		super(makeProperties());
	}

	public static class Source extends XPFluid {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends XPFluid {
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}
	}
}
