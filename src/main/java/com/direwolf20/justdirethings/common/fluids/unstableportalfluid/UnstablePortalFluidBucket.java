package com.direwolf20.justdirethings.common.fluids.unstableportalfluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Forge's bucket only vaporizes in ultra-warm dimensions, so ask the fluid everywhere.
 */
public class UnstablePortalFluidBucket extends BucketItem {
	public UnstablePortalFluidBucket(Supplier<? extends Fluid> fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos,
			@Nullable BlockHitResult hitResult, @Nullable ItemStack container) {
		Fluid fluid = getFluid();
		FluidStack fluidStack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
		if (level.getBlockState(pos).canBeReplaced(fluid) && fluid.getAttributes().doesVaporize(level, pos, fluidStack)) {
			fluid.getAttributes().vaporize(player, level, pos, fluidStack);
			return true;
		}
		return super.emptyContents(player, level, pos, hitResult, container);
	}
}
