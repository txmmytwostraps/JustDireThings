package com.direwolf20.justdirethings.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Random;
import java.util.function.Function;

public class MiscHelpers {
	public enum RedstoneMode {
		IGNORED, LOW, HIGH, PULSE;

		public RedstoneMode next() {
			RedstoneMode[] values = values();
			int nextOrdinal = (this.ordinal() + 1) % values.length;
			return values[nextOrdinal];
		}
	}
	private static final Random rand = new Random();

	public static double nextDouble(double min, double max) {
		return min + (max - min) * rand.nextDouble();
	}

	public static IItemHandler getAttachedInventory(Level level, BlockPos blockPos, Direction side) {
		if (level == null) return null;
		BlockEntity be = level.getBlockEntity(blockPos);
		// if we have a TE and its an item handler, try extracting from that
		if (be != null) {
			return be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
		}
		return null;
	}

	public static Direction getPrimaryDirection(Vec3 vec) {
		double absX = Math.abs(vec.x);
		double absY = Math.abs(vec.y);
		double absZ = Math.abs(vec.z);
		if (absX > absY && absX > absZ) {
			return vec.x > 0 ? Direction.EAST : Direction.WEST;
		} else if (absY > absX && absY > absZ) {
			return vec.y > 0 ? Direction.UP : Direction.DOWN;
		} else {
			return vec.z > 0 ? Direction.SOUTH : Direction.NORTH;
		}
	}

	public static Direction getFacingDirection(Player player) {
		float yaw = player.getYRot();
		float pitch = player.getXRot();
		Direction horizontalDirection = Direction.fromYRot(yaw);
		if (pitch < -45) {
			return Direction.UP;
		} else if (pitch > 45) {
			return Direction.DOWN;
		} else {
			return horizontalDirection;
		}
	}

	// Teleports can move the Wither, the dragon and fishing hooks. An Ender Pearl can only leave the End once its owner has seen the credits.
	public static boolean canChangeDimensions(Entity entity, Level oldLevel, Level newLevel) {
		if (entity instanceof ThrownEnderpearl enderpearl && oldLevel.dimension() == Level.END && enderpearl.getOwner() instanceof ServerPlayer serverPlayer)
			return entity.canChangeDimensions() && serverPlayer.seenCredits;
		return entity.canChangeDimensions() || entity instanceof WitherBoss || entity instanceof EnderDragon || entity instanceof FishingHook;
	}

	// changeDimension resends a player's abilities, effects and XP
	public static boolean teleportEntity(Entity entity, ServerLevel level, double x, double y, double z, float yRot, float xRot) {
		if (entity instanceof ServerPlayer serverPlayer) {
			level.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(new BlockPos(x, y, z)), 1, serverPlayer.getId());
			serverPlayer.stopRiding();
			if (serverPlayer.isSleeping())
				serverPlayer.stopSleepInBed(true, true);
			if (level == serverPlayer.level) {
				serverPlayer.connection.teleport(x, y, z, yRot, xRot);
			} else {
				serverPlayer.setCamera(serverPlayer);
				serverPlayer.changeDimension(level, new ITeleporter() {
					@Override
					public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
						return repositionEntity.apply(false);
					}

					@Override
					public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
						return new PortalInfo(new Vec3(x, y, z), Vec3.ZERO, yRot, xRot);
					}

					@Override
					public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
						return false;
					}
				});
			}
			serverPlayer.setYHeadRot(yRot);
			return true;
		}
		float clampedXRot = Mth.clamp(xRot, -90.0F, 90.0F);
		if (level == entity.level) {
			entity.setYRot(yRot);
			entity.setXRot(clampedXRot);
			entity.teleportTo(x, y, z);
			entity.setYHeadRot(yRot);
		} else {
			entity.unRide();
			Entity movedEntity = entity.getType().create(level);
			if (movedEntity == null)
				return false;
			movedEntity.restoreFrom(entity);
			movedEntity.moveTo(x, y, z, yRot, clampedXRot);
			movedEntity.setYHeadRot(yRot);
			entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
			level.addDuringTeleport(movedEntity);
		}
		return true;
	}

	// Same as the owner hitting the fireball
	public static void redirectFireball(Projectile projectile, Entity hitEntity) {
		Entity owner = projectile.getOwner();
		if (!projectile.level.isClientSide && owner != null && hitEntity.getType() == EntityType.FIREBALL && hitEntity instanceof AbstractHurtingProjectile fireball) {
			Vec3 look = owner.getLookAngle().normalize();
			fireball.setDeltaMovement(look);
			fireball.hasImpulse = true;
			fireball.xPower = look.x * 0.1D;
			fireball.yPower = look.y * 0.1D;
			fireball.zPower = look.z * 0.1D;
			fireball.setOwner(owner);
		}
	}
}
