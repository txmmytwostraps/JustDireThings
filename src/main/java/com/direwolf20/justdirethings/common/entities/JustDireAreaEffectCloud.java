package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JustDireAreaEffectCloud extends Entity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.PARTICLE);
	// The effect particle has no color of its own
	private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.INT);
	private ItemStack potionContents = ItemStack.EMPTY;
	private final Map<Entity, Integer> victims = Maps.newHashMap();
	private int duration = 600;
	private int waitTime = 20;
	private int reapplicationDelay = 20;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusPerTick;
	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUUID;

	public JustDireAreaEffectCloud(EntityType<? extends JustDireAreaEffectCloud> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
	}

	public JustDireAreaEffectCloud(Level level, double x, double y, double z) {
		this(Registration.JustDireAreaEffectCloud.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_RADIUS, 3.0F);
		this.entityData.define(DATA_WAITING, false);
		this.entityData.define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
		this.entityData.define(DATA_COLOR, -1);
	}

	public void setRadius(float radius) {
		if (!this.level.isClientSide) {
			this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
		}
	}

	@Override
	public void refreshDimensions() {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		super.refreshDimensions();
		this.setPos(d0, d1, d2);
	}

	public float getRadius() {
		return this.getEntityData().get(DATA_RADIUS);
	}

	public void setPotionContents(ItemStack potionContents) {
		this.potionContents = potionContents;
		this.updateColor();
	}

	private void updateColor() {
		if (this.getParticle().getType() == ParticleTypes.ENTITY_EFFECT) {
			int i = this.potionContents.isEmpty() ? 0 : PotionCanister.getPotionContentsColor(this.potionContents);
			this.entityData.set(DATA_COLOR, i);
		}
	}

	public ParticleOptions getParticle() {
		return this.getEntityData().get(DATA_PARTICLE);
	}

	public void setParticle(ParticleOptions particleOption) {
		this.getEntityData().set(DATA_PARTICLE, particleOption);
	}

	/**
	 * Sets if the cloud is waiting. While waiting, the radius is ignored and the cloud shows fewer particles in its area.
	 */
	protected void setWaiting(boolean waiting) {
		this.getEntityData().set(DATA_WAITING, waiting);
	}

	public boolean isWaiting() {
		return this.getEntityData().get(DATA_WAITING);
	}

	public int getDuration() {
		return this.duration;
	}

	@Override
	public void tick() {
		super.tick();
		boolean flag = this.isWaiting();
		float f = this.getRadius();
		if (this.level.isClientSide) {
			if (flag && this.random.nextBoolean()) {
				return;
			}

			ParticleOptions particleoptions = this.getParticle();
			int i;
			float f1;
			if (flag) {
				i = 2;
				f1 = 0.2F;
			} else {
				i = Mth.ceil((float) Math.PI * f * f);
				f1 = f;
			}

			for (int j = 0; j < i; j++) {
				float f2 = this.random.nextFloat() * (float) (Math.PI * 2);
				float f3 = Mth.sqrt(this.random.nextFloat()) * f1;
				double d0 = this.getX() + (double) (Mth.cos(f2) * f3);
				double d2 = this.getY();
				double d4 = this.getZ() + (double) (Mth.sin(f2) * f3);
				if (particleoptions.getType() == ParticleTypes.ENTITY_EFFECT) {
					if (flag && this.random.nextBoolean()) {
						this.addEffectParticle(-1, d0, d2, d4);
					} else {
						this.addEffectParticle(this.entityData.get(DATA_COLOR), d0, d2, d4);
					}
				} else if (flag) {
					this.level.addAlwaysVisibleParticle(particleoptions, d0, d2, d4, 0.0, 0.0, 0.0);
				} else {
					this.level
							.addAlwaysVisibleParticle(
									particleoptions, d0, d2, d4, (0.5 - this.random.nextDouble()) * 0.15, 0.01F, (0.5 - this.random.nextDouble()) * 0.15
							);
				}
			}
		} else {
			if (this.tickCount >= this.waitTime + this.duration) {
				this.discard();
				return;
			}

			boolean flag1 = this.tickCount < this.waitTime;
			if (flag != flag1) {
				this.setWaiting(flag1);
			}

			if (flag1) {
				return;
			}

			if (this.radiusPerTick != 0.0F) {
				f += this.radiusPerTick;
				if (f < 0.5F) {
					this.discard();
					return;
				}

				this.setRadius(f);
			}

			if (this.tickCount % 5 == 0) {
				this.victims.entrySet().removeIf(p_287380_ -> this.tickCount >= p_287380_.getValue());
				if (PotionUtils.getMobEffects(this.potionContents).isEmpty()) {
					this.victims.clear();
				} else {
					List<MobEffectInstance> list = Lists.newArrayList();
					for (MobEffectInstance mobeffectinstance1 : PotionUtils.getPotion(this.potionContents).getEffects()) {
						list.add(
								new MobEffectInstance(
										mobeffectinstance1.getEffect(),
										mobeffectinstance1.getDuration() / 4,
										mobeffectinstance1.getAmplifier(),
										mobeffectinstance1.isAmbient(),
										mobeffectinstance1.isVisible()
								)
						);
					}

					list.addAll(PotionUtils.getCustomEffects(this.potionContents));
					List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
					if (!list1.isEmpty()) {
						for (LivingEntity livingentity : list1) {
							if (!this.victims.containsKey(livingentity)
									&& livingentity.isAffectedByPotions()
									&& !list.stream().noneMatch(livingentity::canBeAffected)) {
								double d5 = livingentity.getX() - this.getX();
								double d1 = livingentity.getZ() - this.getZ();
								double d3 = d5 * d5 + d1 * d1;
								if (d3 <= (double) (f * f)) {
									this.victims.put(livingentity, this.tickCount + this.reapplicationDelay);
									boolean affectedAnything = false;
									for (MobEffectInstance mobeffectinstance : list) {
										if (mobeffectinstance.getEffect().getCategory() == MobEffectCategory.HARMFUL && getOwner() != null && livingentity.is(getOwner()))
											continue;
										if (mobeffectinstance.getEffect().getCategory() == MobEffectCategory.BENEFICIAL && getOwner() != null && !livingentity.is(getOwner()))
											continue;
										if (mobeffectinstance.getEffect().isInstantenous()) {
											mobeffectinstance.getEffect()
													.applyInstantenousEffect(this, this.getOwner(), livingentity, mobeffectinstance.getAmplifier(), 0.5);
										} else {
											livingentity.addEffect(new MobEffectInstance(mobeffectinstance), this);
										}
										affectedAnything = true;
									}

									if (this.radiusOnUse != 0.0F && affectedAnything) {
										f += this.radiusOnUse;
										if (f < 0.5F) {
											this.discard();
											return;
										}

										this.setRadius(f);
									}

									if (this.durationOnUse != 0) {
										this.duration = this.duration + this.durationOnUse;
										if (this.duration <= 0) {
											this.discard();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// Speed sets the color, so spawn it still and color it after
	private void addEffectParticle(int color, double x, double y, double z) {
		Particle particle = Minecraft.getInstance().levelRenderer.addParticleInternal(ParticleTypes.ENTITY_EFFECT, false, true, x, y, z, 0.0, 0.0, 0.0);
		if (particle != null)
			particle.setColor((float) (color >> 16 & 0xFF) / 255.0F, (float) (color >> 8 & 0xFF) / 255.0F, (float) (color & 0xFF) / 255.0F);
	}

	public void setRadiusOnUse(float radiusOnUse) {
		this.radiusOnUse = radiusOnUse;
	}

	public void setRadiusPerTick(float radiusPerTick) {
		this.radiusPerTick = radiusPerTick;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUUID();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
			Entity entity = ((ServerLevel) this.level).getEntity(this.ownerUUID);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			}
		}

		return this.owner;
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.tickCount = compound.getInt("Age");
		this.duration = compound.getInt("Duration");
		this.waitTime = compound.getInt("WaitTime");
		this.reapplicationDelay = compound.getInt("ReapplicationDelay");
		this.durationOnUse = compound.getInt("DurationOnUse");
		this.radiusOnUse = compound.getFloat("RadiusOnUse");
		this.radiusPerTick = compound.getFloat("RadiusPerTick");
		this.setRadius(compound.getFloat("Radius"));
		if (compound.hasUUID("Owner")) {
			this.ownerUUID = compound.getUUID("Owner");
		}

		if (compound.contains("Particle", 10)) {
			ParticleTypes.CODEC
					.parse(NbtOps.INSTANCE, compound.get("Particle"))
					.resultOrPartial(p_329991_ -> LOGGER.warn("Failed to parse area effect cloud particle options: '{}'", p_329991_))
					.ifPresent(this::setParticle);
		}

		if (compound.contains("potion_contents")) {
			this.setPotionContents(ItemStack.of(compound.getCompound("potion_contents")));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Age", this.tickCount);
		compound.putInt("Duration", this.duration);
		compound.putInt("WaitTime", this.waitTime);
		compound.putInt("ReapplicationDelay", this.reapplicationDelay);
		compound.putInt("DurationOnUse", this.durationOnUse);
		compound.putFloat("RadiusOnUse", this.radiusOnUse);
		compound.putFloat("RadiusPerTick", this.radiusPerTick);
		compound.putFloat("Radius", this.getRadius());
		compound.put("Particle", ParticleTypes.CODEC.encodeStart(NbtOps.INSTANCE, this.getParticle()).getOrThrow(false, LOGGER::error));
		if (this.ownerUUID != null) {
			compound.putUUID("Owner", this.ownerUUID);
		}

		if (!this.potionContents.isEmpty()) {
			compound.put("potion_contents", this.potionContents.save(new CompoundTag()));
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (DATA_RADIUS.equals(key)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(key);
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
	}
}
