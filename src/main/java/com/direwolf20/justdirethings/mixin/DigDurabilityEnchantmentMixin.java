package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.items.interfaces.BaseToggleableTool;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Wands take no enchantments. Unbreaking would accept anything damageable.
@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin {

	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof BaseToggleableTool)
			cir.setReturnValue(false);
	}
}
