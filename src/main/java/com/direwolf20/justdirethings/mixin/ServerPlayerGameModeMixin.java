package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.client.events.PlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Forge's LeftClickBlock doesn't say which action sent it
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	private void onHandleBlockBreakAction(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, CallbackInfo ci) {
		PlayerEvents.serverBlockActionPlayer = player;
		PlayerEvents.serverBlockAction = action;
	}

	@Inject(method = "handleBlockBreakAction", at = @At("RETURN"))
	private void afterHandleBlockBreakAction(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, CallbackInfo ci) {
		PlayerEvents.serverBlockActionPlayer = null;
	}
}
