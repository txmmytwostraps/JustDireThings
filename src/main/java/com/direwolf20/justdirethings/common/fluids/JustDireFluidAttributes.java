package com.direwolf20.justdirethings.common.fluids;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;

public class JustDireFluidAttributes extends FluidAttributes {
	private static final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "block/fluid_source");
	private static final ResourceLocation FLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "block/fluid_flowing");
	private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(JustDireThings.MODID, "block/fluid_overlay");

	protected JustDireFluidAttributes(Builder builder, Fluid fluid) {
		super(builder, fluid);
	}

	public static Builder builder(int tintColor) {
		return builder(tintColor, JustDireFluidAttributes::new);
	}

	public static Builder builder(int tintColor, BiFunction<Builder, Fluid, FluidAttributes> factory) {
		return new Builder(STILL_TEXTURE, FLOW_TEXTURE, factory) {
		}.overlay(OVERLAY_TEXTURE)
				.color(tintColor)
				.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
	}

	// Material.WATER, but not water, so don't vaporize in ultra-warm dimensions
	@Override
	public boolean doesVaporize(BlockAndTintGetter reader, BlockPos pos, FluidStack fluidStack) {
		return false;
	}
}
