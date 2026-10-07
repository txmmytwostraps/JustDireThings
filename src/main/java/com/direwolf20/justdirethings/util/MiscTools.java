package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MiscTools {
	// Thanks Soaryn!
	@NotNull
	public static BlockHitResult getHitResult(Player player) {
		var playerLook = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
		var lookVec = player.getViewVector(1.0F);
		var reach = player.getReachDistance();
		var endLook = playerLook.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
		BlockHitResult hitResult = player.level
				.clip(new ClipContext(playerLook, endLook, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		return hitResult;
	}

	public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	public static ListTag stringListToNBT(List<String> list) {
		ListTag nbtList = new ListTag();
		for (String string : list) {
			CompoundTag tag = new CompoundTag();
			tag.putString("list", string);
			nbtList.add(tag);
		}
		return nbtList;
	}

	public static List<String> NBTToStringList(ListTag nbtList) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < nbtList.size(); i++) {
			CompoundTag tag = nbtList.getCompound(i);
			list.add(tag.getString("list"));
		}
		return list;
	}

	public static boolean isValidTickAccelBlock(ServerLevel serverLevel, BlockState blockState, @Nullable BlockEntity blockEntity) {
		if (blockEntity == null && !blockState.isRandomlyTicking())
			return false;
		if (blockEntity != null) {
			BlockEntityTicker<BlockEntity> ticker = blockEntity.getBlockState().getTicker(serverLevel, (BlockEntityType<BlockEntity>) blockEntity.getType());
			if (ticker == null)
				return false;
		}
		if (blockState.is(JustDireBlockTags.TICK_SPEED_DENY))
			return false;
		if (isTimeWandBlacklisted(blockState))
			return false;
		return true;
	}

	private static boolean isTimeWandBlacklisted(BlockState state) {
		ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
		if (key == null)
			return false;
		return matchesRegexBlacklist(Config.TIME_WAND_BLOCK_BLACKLIST.get(), key.toString());
	}

	private static final Map<String, Pattern> BLACKLIST_PATTERN_CACHE = new ConcurrentHashMap<>();

	public static boolean matchesRegexBlacklist(List<? extends String> blacklist, String fullId) {
		for (String regex : blacklist) {
			Pattern pattern = BLACKLIST_PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
			if (pattern.matcher(fullId).find())
				return true;
		}
		return false;
	}

	public static boolean isRestrictedDimension(ResourceKey<Level> dimension, ForgeConfigSpec.BooleanValue featureSwitch) {
		return Config.RESTRICTED_DIMENSIONS_ENABLED.get() && featureSwitch.get() && matchesRegexBlacklist(Config.RESTRICTED_DIMENSIONS_LIST.get(), dimension.location().toString());
	}

	public static void doExtraTicks(ServerLevel serverLevel, BlockPos blockPos, double rate) {
		BlockState blockState = serverLevel.getBlockState(blockPos);
		BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
		if (!isValidTickAccelBlock(serverLevel, blockState, blockEntity))
			return;
		for (int i = 0; i < rate; i++) {
			if (blockEntity != null) {
				BlockEntityTicker<BlockEntity> ticker = blockEntity.getBlockState().getTicker(serverLevel, (BlockEntityType<BlockEntity>) blockEntity.getType());
				if (ticker != null) {
					ticker.tick(serverLevel, blockPos, blockEntity.getBlockState(), blockEntity);
				}
			} else if (blockState.isRandomlyTicking()) {
				if (serverLevel.random.nextInt(1365) == 0) { //Average Random Tick Rate
					blockState.randomTick(serverLevel, blockPos, serverLevel.random);
				}
			}
		}
	}

	@Nullable
	public static Entity getEntityLookedAt(Player player, double maxDistance) {
		Vec3 eyePosition = player.getEyePosition(1.0F);
		Vec3 lookVector = player.getViewVector(1.0F).scale(maxDistance);
		Vec3 endPosition = eyePosition.add(lookVector);
		HitResult hitResult = player.level.clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE, player));
		if (hitResult.getType() != HitResult.Type.MISS) {
			endPosition = hitResult.getLocation();
		}
		EntityHitResult entityHitResult = rayTraceEntities(player, eyePosition, endPosition, player.getBoundingBox().expandTowards(lookVector).inflate(1.0D, 1.0D, 1.0D), maxDistance);
		if (entityHitResult != null) {
			return entityHitResult.getEntity();
		}
		return null;
	}

	@Nullable
	private static EntityHitResult rayTraceEntities(Player player, Vec3 start, Vec3 end, AABB boundingBox, double maxDistance) {
		double closestDistance = maxDistance;
		Entity closestEntity = null;
		Vec3 hitLocation = null;
		for (Entity entity : player.level.getEntities(player, boundingBox, e -> e != player && e.isPickable())) {
			AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
			Optional<Vec3> optHitVec = entityBoundingBox.clip(start, end);
			if (entityBoundingBox.contains(start)) {
				if (closestDistance >= 0.0D) {
					closestEntity = entity;
					hitLocation = optHitVec.orElse(start);
					closestDistance = 0.0D;
				}
			} else if (optHitVec.isPresent()) {
				Vec3 hitVec = optHitVec.get();
				double distanceToHitVec = start.distanceTo(hitVec);
				if (distanceToHitVec < closestDistance || closestDistance == 0.0D) {
					if (entity.getRootVehicle() == player.getRootVehicle() && !entity.canRiderInteract()) {
						if (closestDistance == 0.0D) {
							closestEntity = entity;
							hitLocation = hitVec;
						}
					} else {
						closestEntity = entity;
						hitLocation = hitVec;
						closestDistance = distanceToHitVec;
					}
				}
			}
		}
		return closestEntity == null ? null : new EntityHitResult(closestEntity, hitLocation);
	}
}
