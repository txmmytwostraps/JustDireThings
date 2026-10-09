package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
	private void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof Player player && PhaseCollisionHelper.shouldPassThroughWalls(player)) {
			cir.setReturnValue(false);
		}
	}

	// Armor damage protections. Forge has no event here.
	@Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
	private void onIsInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if (LivingEntityEvents.blockDamage((Entity) (Object) this, source))
			cir.setReturnValue(true);
	}
}
