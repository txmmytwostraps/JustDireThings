package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.client.events.PlayerEvents;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// The second post in continueDestroyBlock is a held break, not a new one
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	private static final String LEFT_CLICK_BLOCK = "Lnet/minecraftforge/common/ForgeHooks;onLeftClickBlock(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$LeftClickBlock;";

	@Inject(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = LEFT_CLICK_BLOCK, ordinal = 1, remap = false))
	private void beforeMineHold(BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir) {
		PlayerEvents.clientMineHold = true;
	}

	@Inject(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = LEFT_CLICK_BLOCK, ordinal = 1, shift = At.Shift.AFTER, remap = false))
	private void afterMineHold(BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir) {
		PlayerEvents.clientMineHold = false;
	}
}
