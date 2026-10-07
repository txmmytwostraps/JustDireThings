package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.TotemOfDeathRecall;
import com.direwolf20.justdirethings.common.items.armors.utils.ArmorTiers;
import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.NBTHelpers;
import com.direwolf20.justdirethings.util.UsefulFakePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;


public class LivingEntityEvents {

	// Set by CampfireBlockMixin. Lava Immunity doesn't cover campfires.
	private static Entity campfireDamaged = null;
	private static long campfireDamageTime = 0;

	public static void startCampfireDamage(Entity entity) {
		campfireDamaged = entity;
		campfireDamageTime = entity.level.getGameTime();
	}

	public static void endCampfireDamage() {
		campfireDamaged = null;
	}

	// A leftover mark only counts for its own entity and tick
	private static boolean isCampfireDamage(Entity target) {
		return target == campfireDamaged && target.level.getGameTime() == campfireDamageTime;
	}

	// Called from EntityMixin. True makes the entity invulnerable.
	public static boolean blockDamage(Entity target, DamageSource source) {
		if (target instanceof Player player) {
			ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			if (chestplate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(chestplate, Ability.ELYTRA)) {
				if (source == DamageSource.FLY_INTO_WALL) {
					Helpers.damageTool(chestplate, player, Ability.ELYTRA);
					return true;
				}
			}
			if (chestplate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(chestplate, Ability.LAVAIMMUNITY)) {
				if (source == DamageSource.LAVA || (source == DamageSource.IN_FIRE && !isCampfireDamage(player)) || source == DamageSource.ON_FIRE) {
					Helpers.damageTool(chestplate, player, Ability.LAVAIMMUNITY);
					return true;
				}
			}
			if (chestplate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.hasAbility(Ability.INVULNERABILITY)) {
				int activeCooldown = ToggleableTool.getCooldown(chestplate, Ability.INVULNERABILITY, true);
				if (activeCooldown == -1) return false;
				player.playNotifySound(SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public static void changeTargets(LivingChangeTargetEvent e) {
		LivingEntity source = e.getEntityLiving();
		LivingEntity target = e.getOriginalTarget();
		if (target instanceof Player player) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			if (helmet.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.canUseAbilityAndDurability(helmet, Ability.STUPEFY) && ToggleableTool.getCooldown(helmet, Ability.STUPEFY, true) != -1 && AbilityMethods.getStupefyTargets(helmet).contains(source.getStringUUID())) {
					e.setCanceled(true);
				} else if (toggleableTool.canUseAbilityAndDurability(helmet, Ability.MINDFOG)) {
					double distance = source.position().distanceTo(target.position());
					double defaultRange = source.getAttributes().hasAttribute(Attributes.FOLLOW_RANGE) ? source.getAttribute(Attributes.FOLLOW_RANGE).getValue() : 16;
					int denominator = 2;
					if (helmet.getItem() instanceof ArmorItem armorItem) {
						if (armorItem.getMaterial().equals(ArmorTiers.BLAZEGOLD))
							denominator = 3;
						else if (armorItem.getMaterial().equals(ArmorTiers.CELESTIGEM))
							denominator = 4;
						else if (armorItem.getMaterial().equals(ArmorTiers.ECLIPSEALLOY))
							denominator = 5;
					}
					if (distance > (defaultRange / denominator))
						e.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof Player player) {
			ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
			if (boots.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(boots, Ability.JUMPBOOST))
				Ability.JUMPBOOST.action.execute(player.level, player, boots);
		}
	}

	@SubscribeEvent
	public static void blockJoin(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof UsefulFakePlayer)
			e.setCanceled(true);
	}

	@SubscribeEvent
	public static void LivingFallDamage(LivingFallEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayer player) {
			ItemStack heldItem = player.getMainHandItem();
			if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.hasAbility(Ability.AIRBURST)) {
					event.setDistance(0.0f);
					return;
				}
			}
			heldItem = player.getOffhandItem();
			if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.hasAbility(Ability.AIRBURST)) {
					event.setDistance(0.0f);
					return;
				}
			}
			heldItem = player.getItemBySlot(EquipmentSlot.FEET);
			if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.canUseAbilityAndDurability(heldItem, Ability.NEGATEFALLDAMAGE)) {
					int distance = (int) event.getDistance();
					Helpers.damageTool(heldItem, player, Ability.NEGATEFALLDAMAGE, distance);
					event.setDistance(0.0f);
					return;
				}
			}
			if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.canUseAbilityAndDurability(heldItem, Ability.JUMPBOOST)) {
					int jumpBoost = ToggleableTool.getToolValue(heldItem, Ability.JUMPBOOST.getName());
					event.setDistance(event.getDistance() - jumpBoost);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void LivingDropsEvent(LivingDropsEvent event) {
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof Player player) {
			ItemStack mainHand = player.getMainHandItem();
			if (mainHand.getItem() instanceof ToggleableTool toggleableTool) {
				if (toggleableTool.canUseAbility(mainHand, Ability.SMOKER)) {
					Iterator<ItemEntity> iterator = event.getDrops().iterator();

					while (iterator.hasNext()) {
						ItemEntity itemEntity = iterator.next();
						boolean[] dropSmoked = new boolean[1];
						Helpers.smokeDrop((ServerLevel) player.level, itemEntity, mainHand, event.getEntityLiving(), dropSmoked);

						if (dropSmoked[0]) {
							ToggleableTool.smokerParticles((ServerLevel) player.level, itemEntity.blockPosition(), itemEntity.getItem().getCount());
						}
					}
				}
				if (toggleableTool.canUseAbility(mainHand, Ability.DROPTELEPORT)) {
					IItemHandler handler = ToggleableTool.getBoundHandler((ServerLevel) player.level, mainHand);
					if (handler != null) {
						Iterator<ItemEntity> iterator = event.getDrops().iterator();
						while (iterator.hasNext()) {
							ItemEntity itemEntity = iterator.next();
							ItemStack stack = itemEntity.getItem();

							ItemStack leftover = Helpers.teleportDrop(stack, handler, mainHand, player);

							if (leftover.isEmpty()) {
								// If the stack is now empty, remove the ItemEntity from the collection
								iterator.remove(); // Optionally remove from your collection if it's being directly manipulated
							} else {
								// Otherwise, update the ItemEntity with the modified stack
								itemEntity.setItem(leftover);
							}
						}
						if (event.getDrops().isEmpty()) { //Only spawn particles if we teleported everything - not perfect but better than exhaustive testing
							ToggleableTool.teleportParticles((ServerLevel) player.level, event.getEntityLiving().getPosition(0f));
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	// Powered armor in the current hit takes 100 times the damage
	private static final Set<ItemStack> poweredArmorHit = Collections.newSetFromMap(new IdentityHashMap<>());
	private static Player armorHitPlayer = null;
	private static long armorHitTime = 0;

	// Called from InventoryMixin before armor takes a hit
	public static void onArmorDamage(Player player, DamageSource source, float damageAmount, int[] slots) {
		poweredArmorHit.clear();
		armorHitPlayer = player;
		armorHitTime = player.level.getGameTime();
		if (damageAmount <= 0.0F) return;
		int originalDamage = (int) Math.max(1.0F, damageAmount / 4.0F);
		for (int slot : slots) {
			ItemStack armorItemStack = player.getInventory().armor.get(slot);
			if (!(armorItemStack.getItem() instanceof ArmorItem) || (source.isFire() && armorItemStack.getItem().isFireResistant())) continue;
			if (armorItemStack.getItem() instanceof PoweredItem) {
				poweredArmorHit.add(armorItemStack);
				continue;
			} //No need for below checks for powered items, as those don't break :)
			if (armorItemStack.getItem() instanceof ToggleableTool toggleableTool) {
				float newDmg = armorItemStack.getDamageValue() + originalDamage;
				if (newDmg >= armorItemStack.getMaxDamage()) {
					EnumSet<Ability> abilities = toggleableTool.getAbilities();
					for (Ability ability : abilities) {
						if (ToggleableTool.hasUpgrade(armorItemStack, ability) && ability.requiresUpgrade()) {
							ItemStack upgradeStack = new ItemStack(ability.getUpgradeItem().get());
							player.drop(upgradeStack, true);
						}
					}
				}
			}
		}
	}

	// Called from InventoryMixin after the hit
	public static void finishArmorDamage() {
		poweredArmorHit.clear();
		armorHitPlayer = null;
	}

	// Used by the base armors' damageItem. A leftover mark only counts for its own player and tick.
	public static int armorDamage(ItemStack armorItemStack, int amount, LivingEntity entity) {
		if (entity == armorHitPlayer && entity.level.getGameTime() == armorHitTime && poweredArmorHit.remove(armorItemStack))
			return amount * 100;
		return amount;
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof ServerPlayer player) {
			ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			int currentCooldown = ToggleableTool.getAnyCooldown(chestplate, Ability.DEATHPROTECTION);
			if (currentCooldown == -1) {
				if (chestplate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(chestplate, Ability.DEATHPROTECTION)) {
					AbilityParams abilityParams = toggleableTool.getAbilityParams(Ability.DEATHPROTECTION);
					ToggleableTool.addCooldown(chestplate, Ability.DEATHPROTECTION, abilityParams.cooldown, false);
					player.playNotifySound(SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
					Helpers.damageTool(chestplate, player, Ability.DEATHPROTECTION);
					player.setHealth(10.0F);
					event.setCanceled(true);
					return;
				}
			}
			// Check player's inventory for the totem
			ItemStack totemStack = findTotem(player);
			if (!totemStack.isEmpty()) {
				player.getPersistentData().put("direDeathData", NBTHelpers.globalVec3ToNBT(player.level.dimension(), player.position()));
				totemStack.shrink(1);
			}
		}
	}

	private static ItemStack findTotem(ServerPlayer player) {
		for (ItemStack itemStack : player.getInventory().items) {
			if (itemStack.getItem() == Registration.TotemOfDeathRecall.get() && TotemOfDeathRecall.getBoundTo(itemStack) == null) {
				return itemStack;
			}
		}
		return ItemStack.EMPTY;
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();
		if (oldPlayer.level.isClientSide || !event.isWasDeath()) return;
		ServerPlayer newPlayer = (ServerPlayer) event.getPlayer();
		CompoundTag deathData = oldPlayer.getPersistentData();

		if (deathData.contains("direDeathData")) {
			NBTHelpers.GlobalVec3 boundTo = NBTHelpers.nbtToGlobalVec3(deathData.getCompound("direDeathData"));
			ItemStack totemStack = new ItemStack(Registration.TotemOfDeathRecall.get());
			TotemOfDeathRecall.setBoundTo(totemStack, boundTo);
			newPlayer.getInventory().add(totemStack);
		}
	}
}
