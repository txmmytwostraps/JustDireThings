package com.direwolf20.justdirethings.common.fluids.unstableportalfluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.datagen.JustDireBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Forge only asks {@link #doesVaporize} in ultra-warm dimensions, so callers must ask too.
 */
public class UnstablePortalFluidAttributes extends JustDireFluidAttributes {
	protected UnstablePortalFluidAttributes(Builder builder, Fluid fluid) {
		super(builder, fluid);
	}

	/**
	 * Forge's rule, plus this fluid's biome check.
	 */
	public static boolean vaporizesOnPlacement(Level level, BlockPos pos, FluidStack fluidStack) {
		FluidAttributes attributes = fluidStack.getFluid().getAttributes();
		return (level.dimensionType().ultraWarm() || attributes instanceof UnstablePortalFluidAttributes) && attributes.doesVaporize(level, pos, fluidStack);
	}

	@Override
	public boolean doesVaporize(BlockAndTintGetter reader, BlockPos pos, FluidStack fluidStack) {
		return reader instanceof LevelReader level
				&& !level.getBiome(pos).is(JustDireBiomeTags.UNSTABLE_PORTAL_FLUID_VIABLE);
	}

	@Override
	public void vaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack fluidStack) {
		level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
				2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l)
			level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, (double) pos.getX() + Math.random(),
					(double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
	}
}
