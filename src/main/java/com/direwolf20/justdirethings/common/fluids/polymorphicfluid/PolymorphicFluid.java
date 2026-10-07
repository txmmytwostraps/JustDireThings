package com.direwolf20.justdirethings.common.fluids.polymorphicfluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class PolymorphicFluid extends ForgeFlowingFluid {

	private static ForgeFlowingFluid.Properties makeProperties() {
		return new ForgeFlowingFluid.Properties(() -> Registration.POLYMORPHIC_FLUID_SOURCE.get(),
				() -> Registration.POLYMORPHIC_FLUID_FLOWING.get(),
				JustDireFluidAttributes.builder(0xFFFFFFFF).translationKey("fluid_type.justdirethings.polymorphic_fluid_type")
						.density(1000).viscosity(1000).temperature(300))
				.bucket(() -> Registration.POLYMORPHIC_FLUID_BUCKET.get())
				.block(() -> Registration.POLYMORPHIC_FLUID_BLOCK.get());
	}

	protected PolymorphicFluid() {
		super(makeProperties());
	}

	public static class Source extends PolymorphicFluid {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends PolymorphicFluid {
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
