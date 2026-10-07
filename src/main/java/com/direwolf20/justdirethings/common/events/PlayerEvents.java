package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.PolymorphicWand;
import com.direwolf20.justdirethings.common.items.PolymorphicWandV2;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredTool;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getInstantRFCost;
import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getToolValue;

public class PlayerEvents {
	public static final AttributeModifier stepHeight = new AttributeModifier(UUID.fromString("dac96a09-4758-419d-aa1b-83a27d266484"), "justdirestepassist", 1.0, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier phase = new AttributeModifier(UUID.fromString("3b4b2d28-8f5c-4c2e-9d3a-1a2b3c4d5e6f"), "justdirephase", 1.0, AttributeModifier.Operation.ADDITION);

	// Tracks which players have flight granted specifically by JDT, so we don't
	// revoke mayfly granted by other mods (e.g. AttributeLib/Apotheosis).
	private static final Set<UUID> jdtFlightGranted = new HashSet<>();

	@SubscribeEvent
	public static void PlayerDestroyItem(PlayerDestroyItemEvent event) {
		ItemStack itemStack = event.getOriginal();
		if (itemStack.getItem() instanceof ToggleableTool toggleableTool) {
			EnumSet<Ability> abilities = toggleableTool.getAbilities();
			for (Ability ability : abilities) {
				if (ToggleableTool.hasUpgrade(itemStack, ability) && ability.requiresUpgrade() && itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
					ItemStack upgradeStack = new ItemStack(ability.getUpgradeItem().get());
					event.getPlayer().drop(upgradeStack, true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void ItemAttributes(ItemAttributeModifierEvent event) {
		ItemStack itemStack = event.getItemStack();
		if (itemStack.getItem() instanceof ToggleableTool toggleableTool) {
			if (event.getSlotType() == EquipmentSlot.FEET && toggleableTool.canUseAbility(itemStack, Ability.STEPHEIGHT))
				event.addModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), stepHeight);
			if (event.getSlotType() == EquipmentSlot.LEGS && toggleableTool.canUseAbility(itemStack, Ability.PHASE))
				event.addModifier(Registration.PHASE.get(), phase);
		}
		//A catch all for unpowered itemse
		if (itemStack.getItem() instanceof PoweredTool poweredTool && PoweredItem.getAvailableEnergy(itemStack) < poweredTool.getBlockBreakFECost()) {
			event.clearModifiers();
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide())
			return;
		Player player = event.player;
		if (hasFlight(player)) {
			if (!player.getAbilities().mayfly) {
				player.getAbilities().mayfly = true;
				player.onUpdateAbilities();
			}
			jdtFlightGranted.add(player.getUUID());
		} else if (!player.isCreative() && !player.isSpectator()) {
			if (jdtFlightGranted.remove(player.getUUID())) {
				player.getAbilities().mayfly = false;
				player.getAbilities().flying = false;
				player.onUpdateAbilities();
			}
		}
	}

	/**
	 * Changing game mode stops flight until the chestplate's next tick. This keeps a flying player in the air.
	 * Called from ServerPlayerGameModeFlightMixin.
	 */
	public static void keepFlying(Player player) {
		if (hasFlight(player)) {
			player.getAbilities().flying = true;
			jdtFlightGranted.add(player.getUUID());
		}
	}

	private static boolean hasFlight(Player player) {
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return chestplate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(chestplate, Ability.FLIGHT);
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		jdtFlightGranted.remove(event.getPlayer().getUUID());
	}

	@SubscribeEvent
	public static void BreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getPlayer();
		float targetSpeed = event.getNewSpeed();
		if (player.getAbilities().flying && event.getNewSpeed() < event.getOriginalSpeed() * 5.0F) { //Prevent this combining with other mods like Apotheosis
			ItemStack chestPlate = player.getItemBySlot(EquipmentSlot.CHEST);
			if (chestPlate.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(chestPlate, Ability.FLIGHT))
				targetSpeed = targetSpeed * 5; //Vanilla divides by 5 if you're flying
		}
		ItemStack stack = player.getMainHandItem(); // Assuming the tool is in the main hand
		int rfCost = 0;
		if (stack.getItem() instanceof ToggleableTool toggleableTool && stack.isCorrectToolForDrops(event.getState())) {
			if (stack.getItem() instanceof PoweredTool poweredTool) {
				if (PoweredItem.getAvailableEnergy(stack) < poweredTool.getBlockBreakFECost()) {
					event.setNewSpeed(0.1f);
					return;
				}
			}
			Level level = player.level;
			BlockPos originalPos = event.getPos();
			BlockState originalState = level.getBlockState(event.getPos());
			float originalDestroySpeed = originalState.getDestroySpeed(level, originalPos);
			float cumulativeDestroy = 0;
			if (originalDestroySpeed <= 0) return;
			Set<BlockPos> breakBlockPositions = toggleableTool.getBreakBlockPositions(stack, level, originalPos, player, originalState);
			if (!breakBlockPositions.isEmpty()) { //Avoid potential divide by zero
				int radius = toggleableTool.canUseAbility(stack, Ability.HAMMER) ? getToolValue(stack, Ability.HAMMER.getName()) : 1;
				for (BlockPos pos : breakBlockPositions) {
					BlockState blockState = level.getBlockState(pos);
					float destroySpeedTarget = blockState.getDestroySpeed(level, pos);
					cumulativeDestroy = cumulativeDestroy + destroySpeedTarget;
				}
				rfCost = getInstantRFCost(cumulativeDestroy, level, stack);
				float modifier = ((float) breakBlockPositions.size() / radius) < 1 ? 1 : ((float) breakBlockPositions.size() / radius);
				cumulativeDestroy = (cumulativeDestroy / breakBlockPositions.size()) * modifier; //Up to 3 times slower
				float relative = originalDestroySpeed / cumulativeDestroy;
				targetSpeed = targetSpeed * relative;
			}
			if (toggleableTool.canUseAbility(stack, Ability.INSTABREAK) && stack.getItem() instanceof PoweredItem && PoweredItem.getAvailableEnergy(stack) >= rfCost) {
				targetSpeed = 10000f;
			}
		}
		if (targetSpeed != event.getNewSpeed())
			event.setNewSpeed(targetSpeed);
	}

	@SubscribeEvent
	public static void CancelEntityInteraction(PlayerInteractEvent.EntityInteract event) {
		ItemStack itemStack = event.getItemStack();
		if (itemStack.getItem() instanceof PolymorphicWand || itemStack.getItem() instanceof PolymorphicWandV2)
			event.setCanceled(true);
	}
}
