package com.direwolf20.justdirethings.common.fluids.unrefinedt4fuel;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class UnrefinedT4Fuel extends ForgeFlowingFluid {

	private static ForgeFlowingFluid.Properties makeProperties() {
		return new ForgeFlowingFluid.Properties(() -> Registration.UNREFINED_T4_FLUID_SOURCE.get(),
				() -> Registration.UNREFINED_T4_FLUID_FLOWING.get(),
				JustDireFluidAttributes.builder(0xFF36484A).translationKey("fluid_type.justdirethings.unrefined_t4_fluid_type")
						.density(1000).viscosity(1000).temperature(300))
				.bucket(() -> Registration.UNREFINED_T4_FLUID_BUCKET.get())
				.block(() -> Registration.UNREFINED_T4_FLUID_BLOCK.get());
	}

	protected UnrefinedT4Fuel() {
		super(makeProperties());
	}

	public static class Source extends UnrefinedT4Fuel {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends UnrefinedT4Fuel {
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
