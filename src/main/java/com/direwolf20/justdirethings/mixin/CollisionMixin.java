package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Lets a phasing player walk through walls. Interface mixins can't inject, so these are overwrites.
 */
@Mixin(CollisionGetter.class)
public interface CollisionMixin {

	/**
	 * @author txmmytwostraps
	 * @reason Phase skips suffocating blocks
	 */
	@Overwrite
	default boolean collidesWithSuffocatingBlock(@Nullable Entity entity, AABB box) {
		if (entity instanceof Player player && PhaseCollisionHelper.shouldPassThroughWalls(player))
			return false;

		BlockCollisions blockCollisions = new BlockCollisions((CollisionGetter) (Object) this, entity, box, true);
		while (blockCollisions.hasNext()) {
			if (!blockCollisions.next().isEmpty())
				return true;
		}
		return false;
	}

	/**
	 * @author txmmytwostraps
	 * @reason Phase walks through walls
	 */
	@Overwrite
	default Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB collisionBox) {
		Iterable<VoxelShape> originalBlockCollisions = () -> new BlockCollisions((CollisionGetter) (Object) this, entity, collisionBox);
		if (entity instanceof Player player && PhaseCollisionHelper.shouldPassThroughWalls(player)) {
			List<VoxelShape> filteredBlockCollisions = StreamSupport.stream(originalBlockCollisions.spliterator(), false)
					.filter(shape -> PhaseCollisionHelper.isVerticalCollision(shape, collisionBox, player))
					.collect(Collectors.toList());

			return filteredBlockCollisions;
		}
		return originalBlockCollisions;
	}
}
