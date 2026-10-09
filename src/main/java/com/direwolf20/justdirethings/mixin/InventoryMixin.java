package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Forge has no armor damage event
@Mixin(Inventory.class)
public abstract class InventoryMixin {

	@Inject(method = "hurtArmor", at = @At("HEAD"))
	private void beforeHurtArmor(DamageSource source, float amount, int[] slots, CallbackInfo ci) {
		Inventory inventory = (Inventory) (Object) this;
		if (!inventory.player.level.isClientSide)
			LivingEntityEvents.onArmorDamage(inventory.player, source, amount, slots);
	}

	@Inject(method = "hurtArmor", at = @At("RETURN"))
	private void afterHurtArmor(DamageSource source, float amount, int[] slots, CallbackInfo ci) {
		Inventory inventory = (Inventory) (Object) this;
		if (!inventory.player.level.isClientSide)
			LivingEntityEvents.finishArmorDamage();
	}
}
