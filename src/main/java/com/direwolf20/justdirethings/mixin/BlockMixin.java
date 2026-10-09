package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

// Lets tools handle block drops. Forge has no event for it.
@Mixin(Block.class)
public abstract class BlockMixin {

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
	private static void onDropResources(BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo ci) {
		if (BlockEvents.BlockDrops(state, level, pos, blockEntity, entity, tool))
			ci.cancel();
	}
}
