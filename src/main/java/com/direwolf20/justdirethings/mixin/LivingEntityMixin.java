package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.NightVisionHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Helmet Night Vision, see GameRendererMixin
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
	private void onHasEffect(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
		if (effect == MobEffects.NIGHT_VISION && NightVisionHelper.hasNightVision((LivingEntity) (Object) this))
			cir.setReturnValue(true);
	}
}
