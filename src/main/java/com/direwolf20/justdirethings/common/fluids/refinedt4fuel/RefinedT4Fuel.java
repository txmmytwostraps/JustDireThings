package com.direwolf20.justdirethings.common.fluids.refinedt4fuel;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.common.fluids.basefluids.RefinedFuel;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class RefinedT4Fuel extends ForgeFlowingFluid implements RefinedFuel {

	private static ForgeFlowingFluid.Properties makeProperties() {
		return new ForgeFlowingFluid.Properties(() -> Registration.REFINED_T4_FLUID_SOURCE.get(),
				() -> Registration.REFINED_T4_FLUID_FLOWING.get(),
				JustDireFluidAttributes.builder(0xFF1B2027).translationKey("fluid_type.justdirethings.refined_t4_fluid_type")
						.density(1000).viscosity(1000).temperature(300))
				.bucket(() -> Registration.REFINED_T4_FLUID_BUCKET.get())
				.block(() -> Registration.REFINED_T4_FLUID_BLOCK.get());
	}

	protected RefinedT4Fuel() {
		super(makeProperties());
	}

	@Override
	public int fePerMb() {
		return Config.FUEL_TIER4_FE_PER_MB.get();
	}

	public static class Source extends RefinedT4Fuel {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends RefinedT4Fuel {
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
