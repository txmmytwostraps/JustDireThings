package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.items.tools.basetools.BasePaxel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Paxels take an axe's damage enchantments, except at the enchanting table
@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin {

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof BasePaxel)
			cir.setReturnValue(true);
	}
}
