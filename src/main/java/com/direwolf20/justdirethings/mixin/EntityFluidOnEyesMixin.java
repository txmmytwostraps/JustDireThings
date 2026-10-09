package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.FluidOnEyesHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tracks the fluid at the eyes the way vanilla checks water. Drives the mod's fluid overlay.
 */
@Mixin(Entity.class)
public abstract class EntityFluidOnEyesMixin implements FluidOnEyesHolder {
	@Unique
	private Fluid justdirethings$fluidOnEyes = Fluids.EMPTY;

	@Inject(method = "updateFluidOnEyes", at = @At("HEAD"))
	private void onUpdateFluidOnEyes(CallbackInfo ci) {
		justdirethings$fluidOnEyes = Fluids.EMPTY;
	}

	@ModifyVariable(method = "updateFluidOnEyes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getTags()Ljava/util/stream/Stream;"))
	private FluidState onFluidOnEyes(FluidState fluidState) {
		justdirethings$fluidOnEyes = fluidState.getType();
		return fluidState;
	}

	@Override
	public Fluid justdirethings$getFluidOnEyes() {
		return justdirethings$fluidOnEyes;
	}
}
