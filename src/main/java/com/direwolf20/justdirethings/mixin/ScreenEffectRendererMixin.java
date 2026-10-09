package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidAttributes;
import com.direwolf20.justdirethings.util.FluidOnEyesHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.tags.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Underwater overlay for the mod's fluids, drawn where water's would be.
 */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
	@Shadow
	private static void renderWater(Minecraft minecraft, PoseStack poseStack) {
		throw new AssertionError();
	}

	@Inject(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isOnFire()Z"))
	private static void onRenderScreenEffect(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
		if (!minecraft.player.isEyeInFluid(FluidTags.WATER) && ((FluidOnEyesHolder) minecraft.player).justdirethings$getFluidOnEyes().getAttributes() instanceof JustDireFluidAttributes) {
			renderWater(minecraft, poseStack);
			// Without this, the fire overlay comes out faded
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
