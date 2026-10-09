package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Campfires deal fire damage. Mark it so Lava Immunity can tell them apart.
@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

	@Inject(method = "entityInside", at = @At("HEAD"))
	private void beforeEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (!level.isClientSide)
			LivingEntityEvents.startCampfireDamage(entity);
	}

	@Inject(method = "entityInside", at = @At("RETURN"))
	private void afterEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (!level.isClientSide)
			LivingEntityEvents.endCampfireDamage();
	}
}
