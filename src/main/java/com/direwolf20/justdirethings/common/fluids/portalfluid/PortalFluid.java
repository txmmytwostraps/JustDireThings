package com.direwolf20.justdirethings.common.fluids.portalfluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class PortalFluid extends ForgeFlowingFluid {

	private static ForgeFlowingFluid.Properties makeProperties() {
		return new ForgeFlowingFluid.Properties(() -> Registration.PORTAL_FLUID_SOURCE.get(),
				() -> Registration.PORTAL_FLUID_FLOWING.get(),
				JustDireFluidAttributes.builder(0xFF00DD00).translationKey("fluid_type.justdirethings.portal_fluid_type")
						.density(1000).viscosity(1000).temperature(300))
				.bucket(() -> Registration.PORTAL_FLUID_BUCKET.get())
				.block(() -> Registration.PORTAL_FLUID_BLOCK.get());
	}

	protected PortalFluid() {
		super(makeProperties());
	}

	public static class Source extends PortalFluid {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends PortalFluid {
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
