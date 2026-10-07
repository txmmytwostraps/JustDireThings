package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Shared wall-vs-floor collision logic for the Phase ability mixins. Must NOT
 * live in the mixin package: classes there can't be referenced from
 * mixin-transformed code at runtime.
 */
public final class PhaseCollisionHelper {
	private PhaseCollisionHelper() {
	}

	public static boolean shouldPassThroughWalls(Player player) {
		return player.getAttributeValue(Registration.PHASE.get()) > 0 && !MiscTools.isRestrictedDimension(player.level.dimension(), Config.RESTRICT_PHASE);
	}

	public static boolean isVerticalCollision(VoxelShape shape, AABB collisionBox, Player player) {
		Level level = player.level;
		BlockPos blockPos = new BlockPos((int) shape.min(Direction.Axis.X), (int) shape.min(Direction.Axis.Y), (int) shape.min(Direction.Axis.Z));
		BlockState blockState = level.getBlockState(blockPos);
		if (blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY))
			return true;
		double maxY = shape.max(Direction.Axis.Y);
		double minY = collisionBox.minY;

		boolean isDifferenceLessThanPointTwo = Math.abs(maxY - minY) < 0.75;
		return isDifferenceLessThanPointTwo; // Ignore vertical collisions
	}
}
