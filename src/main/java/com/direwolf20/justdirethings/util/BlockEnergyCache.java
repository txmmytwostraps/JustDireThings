package com.direwolf20.justdirethings.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Saves machines a capability lookup every tick. Entries drop out when their {@link LazyOptional} is invalidated.
 */
public class BlockEnergyCache {

	/**
	 * Key composed of an immutable {@link BlockPos} and the query {@link Direction}
	 * (nullable for isotropic). Records provide structural equals/hashCode
	 * automatically.
	 */
	private record CacheKey(BlockPos pos, @Nullable Direction side) {
	}

	private final Map<CacheKey, LazyOptional<IEnergyStorage>> cache = new HashMap<>();

	/**
	 * Returns the {@link IEnergyStorage} at {@code pos} queried from {@code side},
	 * using a cached {@link LazyOptional} when still valid.
	 *
	 * @param level
	 *            the level to query
	 * @param pos
	 *            the target block position
	 * @param side
	 *            the side to query from ({@code null} = isotropic / side-agnostic
	 *            query)
	 * @return the energy storage, or {@code null} if unavailable
	 */
	@Nullable
	public IEnergyStorage get(Level level, BlockPos pos, @Nullable Direction side) {
		CacheKey key = new CacheKey(pos.immutable(), side);
		LazyOptional<IEnergyStorage> cached = cache.get(key);

		// Cache hit — optional is still valid
		if (cached != null && cached.isPresent()) {
			return cached.orElse(null);
		}

		// Cache miss or invalidated — re-query the block entity
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) {
			cache.remove(key);
			return null;
		}

		LazyOptional<IEnergyStorage> cap = be.getCapability(CapabilityEnergy.ENERGY, side);
		cache.remove(key); // evict any stale entry
		if (cap.isPresent()) {
			cache.put(key, cap);
			// When the neighbor calls invalidateCaps() the LazyOptional is invalidated,
			// which fires this listener and auto-evicts our cache entry.
			cap.addListener(opt -> cache.remove(key));
		}
		return cap.orElse(null);
	}

	/**
	 * Eagerly invalidate all cached entries for a specific position (across all
	 * sides). Use when you know a particular block has changed.
	 */
	public void invalidatePos(BlockPos pos) {
		BlockPos immPos = pos.immutable();
		cache.entrySet().removeIf(e -> Objects.equals(e.getKey().pos(), immPos));
	}
}
