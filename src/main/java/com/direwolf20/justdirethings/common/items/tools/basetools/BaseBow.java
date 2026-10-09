package com.direwolf20.justdirethings.common.items.tools.basetools;

import com.direwolf20.justdirethings.common.capabilities.ItemStackItemHandler;
import com.direwolf20.justdirethings.common.entities.JustDireArrow;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class BaseBow extends BowItem implements ToggleableTool, LeftClickableTool {
	protected final EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	protected final Map<Ability, AbilityParams> abilityParams = new EnumMap<>(Ability.class);

	public BaseBow(Properties properties) {
		super(properties);
	}

	public float getMaxDraw() {
		return 20;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level.isClientSide) return InteractionResultHolder.pass(stack);
		if (player.isShiftKeyDown()) {
			openSettings(player);
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}
		if (stack.getItem() instanceof PoweredTool poweredTool && PoweredItem.getAvailableEnergy(stack) < poweredTool.getBlockBreakFECost())
			return InteractionResultHolder.pass(stack);
		return super.use(level, player, hand);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected) {
		if ((!getPassiveTickAbilities(itemStack).isEmpty() || !getCooldownAbilities().isEmpty()) && entity instanceof Player player) {
			ToggleableTool.tickCooldowns(level, itemStack, player);
		}
	}

	// BowItem's release hardcodes the arrow and the durability cost
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
		if (!(livingEntity instanceof Player player))
			return;
		boolean infiniteBow = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
		ItemStack ammo = player.getProjectile(stack);
		int charge = ForgeEventFactory.onArrowLoose(stack, level, player, this.getUseDuration(stack) - timeLeft, !ammo.isEmpty() || infiniteBow);
		if (charge < 0 || (ammo.isEmpty() && !infiniteBow))
			return;
		if (ammo.isEmpty())
			ammo = new ItemStack(Items.ARROW);

		float power = getPowerForTime(charge);
		if ((double) power < 0.1D)
			return;
		boolean infiniteAmmo = player.getAbilities().instabuild || (ammo.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(ammo, stack, player));
		if (!level.isClientSide) {
			AbstractArrow abstractArrow = createProjectile(level, player, stack, ammo, power == 1.0F);
			abstractArrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
			int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
			if (powerLevel > 0)
				abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
			int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
			if (punchLevel > 0)
				abstractArrow.setKnockback(punchLevel);
			if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0)
				abstractArrow.setSecondsOnFire(100);
			int durabilityUse = getDurabilityUse(ammo);
			Helpers.damageItemInCreative(stack, durabilityUse, player);
			stack.hurtAndBreak(durabilityUse, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
			if (infiniteAmmo || player.getAbilities().instabuild && (ammo.is(Items.SPECTRAL_ARROW) || ammo.is(Items.TIPPED_ARROW)))
				abstractArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			level.addFreshEntity(abstractArrow);
		}

		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
		if (!infiniteAmmo && !player.getAbilities().instabuild) {
			ammo.shrink(1);
			if (ammo.isEmpty())
				player.getInventory().removeItem(ammo);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
	}

	protected AbstractArrow createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack stack, boolean crit) {
		ArrowItem arrowitem = stack.getItem() instanceof ArrowItem arrowitem1 ? arrowitem1 : (ArrowItem) Items.ARROW;
		ToggleableTool toggleableTool = (ToggleableTool) itemStack.getItem();
		if (arrowitem.equals(Items.ARROW)) {
			JustDireArrow justDireArrow = new JustDireArrow(level, livingEntity, stack, itemStack);
			if (crit) {
				justDireArrow.setCritArrow(true);
			}
			CompoundTag tag = itemStack.getTag();
			if (tag != null && tag.getBoolean(AbilityMethods.EPIC_ARROW)) {
				justDireArrow.setEpicArrow(true);
				justDireArrow.setBaseDamage(20d);
				AbilityParams abilityParams = toggleableTool.getAbilityParams(Ability.EPICARROW);
				ToggleableTool.addCooldown(itemStack, Ability.EPICARROW, abilityParams.cooldown, false);
				itemStack.getOrCreateTag().putBoolean(AbilityMethods.EPIC_ARROW, false);
			}

			if (!toggleableTool.getEnabled(itemStack))
				return customArrow(justDireArrow);

			if (canUseAbilityAndDurability(itemStack, Ability.PHASE) && !MiscTools.isRestrictedDimension(level.dimension(), Config.RESTRICT_PHASE)) {
				justDireArrow.setPhase(true);
				Helpers.damageTool(itemStack, livingEntity, Ability.PHASE);
			}

			if (canUseAbilityAndDurability(itemStack, Ability.HOMING)) {
				justDireArrow.setHoming(true);
				Helpers.damageTool(itemStack, livingEntity, Ability.HOMING);
				boolean hostileOnly = ToggleableTool.getCustomSetting(itemStack, Ability.HOMING.getName()) == 0;
				LivingEntity aimedAtEntity = findAimedAtEntity(livingEntity, hostileOnly, justDireArrow);
				if (aimedAtEntity != null)
					justDireArrow.setTargetEntity(aimedAtEntity);
				justDireArrow.setHostileOnly(hostileOnly);
			}

			if (noPotionAbilitiesActive(itemStack))
				return customArrow(justDireArrow);

			IItemHandler itemHandler = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (itemHandler instanceof ItemStackItemHandler itemStackItemHandler) {
				List<MobEffectInstance> potionEffects = new ArrayList<>();
				for (int slot = 0; slot < itemStackItemHandler.getSlots(); slot++) {
					ItemStack potionCanister = itemStackItemHandler.getStackInSlot(slot);
					if (potionCanister.getItem() instanceof PotionCanister) {
						int potionAmt = PotionCanister.getPotionAmount(potionCanister);
						ItemStack slotPotionContents = PotionCanister.getPotionContents(potionCanister);
						if (!slotPotionContents.isEmpty()) {
							int neededAmt = 0;
							if (canUseAbilityAndDurability(itemStack, Ability.POTIONARROW))
								neededAmt = neededAmt + 25;
							if (canUseAbilityAndDurability(itemStack, Ability.SPLASH))
								neededAmt = neededAmt + 25;
							if (canUseAbilityAndDurability(itemStack, Ability.LINGERING))
								neededAmt = neededAmt + 50;
							if (potionAmt >= neededAmt) {
								potionEffects.addAll(PotionUtils.getMobEffects(slotPotionContents));
								PotionCanister.setPotionAmount(potionCanister, potionAmt - neededAmt);
								itemStackItemHandler.setStackInSlot(slot, potionCanister);
							}
						}
					}
				}
				if (!potionEffects.isEmpty()) {
					justDireArrow.setPotionContents(PotionUtils.setCustomEffects(new ItemStack(Items.POTION), potionEffects));
					if (canUseAbilityAndDurability(itemStack, Ability.POTIONARROW)) {
						justDireArrow.setPotionArrow(true);
						Helpers.damageTool(itemStack, livingEntity, Ability.POTIONARROW);
					}
					if (canUseAbilityAndDurability(itemStack, Ability.SPLASH)) {
						justDireArrow.setSplash(true);
						Helpers.damageTool(itemStack, livingEntity, Ability.SPLASH);
					}
					if (canUseAbilityAndDurability(itemStack, Ability.LINGERING)) {
						justDireArrow.setLingering(true);
						Helpers.damageTool(itemStack, livingEntity, Ability.LINGERING);
					}
				}
			}

			return customArrow(justDireArrow);
		}
		AbstractArrow abstractArrow = arrowitem.createArrow(level, stack, livingEntity);
		if (crit) {
			abstractArrow.setCritArrow(true);
		}
		return customArrow(abstractArrow);
	}

	public LivingEntity findAimedAtEntity(LivingEntity livingEntity, boolean onlyHostile, JustDireArrow justDireArrow) {
		double range = 50;
		Vec3 startVec = livingEntity.getEyePosition(1.0F);
		Vec3 lookVec = livingEntity.getViewVector(1.0F);
		Vec3 endVec = startVec.add(lookVec.scale(range));

		// Perform the ray trace
		HitResult hitResult = livingEntity.level.clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));

		if (hitResult.getType() != HitResult.Type.MISS) {
			endVec = hitResult.getLocation();
		}

		AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D);
		List<Entity> entities = livingEntity.level.getEntities(livingEntity, boundingBox, EntitySelector.LIVING_ENTITY_STILL_ALIVE);

		LivingEntity closestEntity = null;
		double closestDistance = range * range;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity livingEntity1) {
				if (onlyHostile && !justDireArrow.isHostileEntity(livingEntity1)) {
					continue;  // Skip non-hostile entities if onlyHostile is true
				}
				AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
				Vec3 entityHitVec = entityBoundingBox.clip(startVec, endVec).orElse(null);

				if (entityBoundingBox.contains(startVec)) {
					if (closestDistance >= 0.0D) {
						closestEntity = livingEntity1;
						closestDistance = 0.0D;
					}
				} else if (entityHitVec != null) {
					double distanceToHit = startVec.distanceToSqr(entityHitVec);

					if (distanceToHit < closestDistance) {
						closestEntity = livingEntity1;
						closestDistance = distanceToHit;
					}
				}
			}
		}
		return closestEntity;
	}

	public boolean noPotionAbilitiesActive(ItemStack itemStack) {
		if (canUseAbilityAndDurability(itemStack, Ability.POTIONARROW))
			return false;
		if (canUseAbilityAndDurability(itemStack, Ability.SPLASH))
			return false;
		if (canUseAbilityAndDurability(itemStack, Ability.LINGERING))
			return false;
		return true;
	}

	protected int getDurabilityUse(ItemStack itemStack) {
		return this instanceof PoweredTool poweredTool ? poweredTool.getBlockBreakFECost() : 1;
	}

	@Override
	public EnumSet<Ability> getAllAbilities() {
		return abilities;
	}

	@Override
	public EnumSet<Ability> getAbilities() {
		return abilities.stream()
				.filter(ability -> Config.AVAILABLE_ABILITY_MAP.get(ability).get())
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(Ability.class)));
	}

	@Override
	public Map<Ability, AbilityParams> getAbilityParamsMap() {
		return abilityParams;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null) {
			return;
		}

		boolean sneakPressed = Screen.hasShiftDown();
		appendFEText(stack, tooltip);
		if (sneakPressed) {
			appendToolEnabled(stack, tooltip);
			appendAbilityList(stack, tooltip);
		} else {
			appendToolEnabled(stack, tooltip);
			appendShiftForInfo(stack, tooltip);
		}
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if (stack.getItem() instanceof PoweredTool) {
			IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return amount;
			int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
			double reductionFactor = Math.min(1.0, unbreakingLevel * 0.1);
			int finalEnergyCost = (int) Math.max(0, amount - (amount * reductionFactor));
			energyStorage.extractEnergy(finalEnergyCost, false);
			return 0;
		}
		return amount;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
}
