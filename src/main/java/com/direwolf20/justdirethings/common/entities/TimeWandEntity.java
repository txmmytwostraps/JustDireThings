package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class TimeWandEntity extends Entity {
	private static final EntityDataAccessor<Integer> TICKSPEED = SynchedEntityData.defineId(TimeWandEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> REMAINING_TIME = SynchedEntityData.defineId(TimeWandEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TOTAL_TIME = SynchedEntityData.defineId(TimeWandEntity.class, EntityDataSerializers.INT);
	private BlockPos blockPos;
	// Its wand's restriction and cap still apply while it runs
	private Item wand = Registration.TimeWand.get();

	public TimeWandEntity(EntityType<? extends Entity> entityType, Level level) {
		super(entityType, level);
	}

	public TimeWandEntity(Level level, BlockPos blockPos, Item wand) {
		this(Registration.TimeWandEntity.get(), level);
		this.blockPos = blockPos;
		this.wand = wand;
		this.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			if (!canSurvive())
				this.remove(RemovalReason.DISCARDED);
			doExtraTicks();
			tickLife();
		}
	}

	public boolean canSurvive() {
		if (blockPos == null)
			return false;
		if (getRemainingTime() < 0)
			return false;
		return true;
	}

	public void doExtraTicks() {
		// Summoned by command, with no block
		if (blockPos == null)
			return;
		if (level instanceof ServerLevel serverLevel) { //Should always be true - mostly just getting serverlevel!
			if (wand == Registration.TimeWand.get() && MiscTools.isRestrictedDimension(serverLevel.dimension(), Config.RESTRICT_TIME_WAND))
				return;
			MiscTools.doExtraTicks(serverLevel, blockPos, getAccelerationRate());
		}
	}

	public void tickLife() {
		setRemainingTime(getRemainingTime() - 1);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(TICKSPEED, 1);
		this.entityData.define(REMAINING_TIME, 600); //30 Seconds Default
		this.entityData.define(TOTAL_TIME, 600); //30 Seconds Default
	}

	public int getTickSpeed() {
		return this.entityData.get(TICKSPEED);
	}

	public int getTotalTime() {
		return this.entityData.get(TOTAL_TIME);
	}

	// A lowered max still caps an older speed
	public float getAccelerationRate() {
		if (wand == Registration.TimeWand.get())
			return calculateAccelRate(Math.min(getTickSpeed(), Config.logBase2(Config.TIME_WAND_MAX_MULTIPLIER.get())));
		return calculateAccelRate(getTickSpeed());
	}

	public static float calculateAccelRate(int speed) {
		return (float) Math.pow(2, speed);
	}

	public void setTickSpeed(int tickSpeed) {
		this.entityData.set(TICKSPEED, tickSpeed);
	}

	public int getRemainingTime() {
		return this.entityData.get(REMAINING_TIME);
	}

	public void setRemainingTime(int remainingTime) {
		this.entityData.set(REMAINING_TIME, remainingTime);
	}

	public void addTime(int moreTime) {
		this.entityData.set(REMAINING_TIME, getRemainingTime() + moreTime);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("tickSpeed"))
			this.entityData.set(TICKSPEED, compound.getInt("tickSpeed"));
		if (compound.contains("remainingTime"))
			this.entityData.set(REMAINING_TIME, compound.getInt("remainingTime"));
		if (compound.contains("totalTime"))
			this.entityData.set(TOTAL_TIME, compound.getInt("totalTime"));
		if (compound.contains("blockpos"))
			this.blockPos = NbtUtils.readBlockPos(compound.getCompound("blockpos"));
		if (compound.contains("wand")) {
			ResourceLocation wandId = ResourceLocation.tryParse(compound.getString("wand"));
			if (wandId != null && ForgeRegistries.ITEMS.containsKey(wandId))
				this.wand = ForgeRegistries.ITEMS.getValue(wandId);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("tickSpeed", getTickSpeed());
		compound.putInt("remainingTime", getRemainingTime());
		if (blockPos != null)
			compound.put("blockpos", NbtUtils.writeBlockPos(blockPos));
		compound.putInt("totalTime", getTotalTime());
		compound.putString("wand", ForgeRegistries.ITEMS.getKey(wand).toString());
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
