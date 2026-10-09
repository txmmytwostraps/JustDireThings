package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Setting a game mode ends flight. This keeps Flight chestplate wearers in the air.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeFlightMixin {
	private static final String UPDATE_PLAYER_ABILITIES = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V";

	@Shadow
	@Final
	protected ServerPlayer player;

	@Unique
	private boolean justdirethings$wasFlying;

	@Inject(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = UPDATE_PLAYER_ABILITIES))
	private void beforeUpdatePlayerAbilities(GameType gameType, GameType previousGameType, CallbackInfo ci) {
		justdirethings$wasFlying = player.getAbilities().flying;
	}

	@Inject(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = UPDATE_PLAYER_ABILITIES, shift = At.Shift.AFTER))
	private void afterUpdatePlayerAbilities(GameType gameType, GameType previousGameType, CallbackInfo ci) {
		if (justdirethings$wasFlying)
			PlayerEvents.keepFlying(player);
	}
}
