package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.NightVisionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Helmet Night Vision, for the light texture only. Marked around the call so mods that replace the update still work.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	private static final String UPDATE_LIGHT_TEXTURE = "Lnet/minecraft/client/renderer/LightTexture;updateLightTexture(F)V";

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = UPDATE_LIGHT_TEXTURE))
	private void beforeUpdateLightTexture(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
		NightVisionHelper.lightTexturePlayer = minecraft.player;
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = UPDATE_LIGHT_TEXTURE, shift = At.Shift.AFTER))
	private void afterUpdateLightTexture(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
		NightVisionHelper.lightTexturePlayer = null;
	}

	@Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
	private static void onGetNightVisionScale(LivingEntity livingEntity, float nanoTime, CallbackInfoReturnable<Float> cir) {
		if (NightVisionHelper.hasNightVision(livingEntity))
			cir.setReturnValue(1f);
	}
}
