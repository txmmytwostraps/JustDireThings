package com.direwolf20.justdirethings.common.items.armors.basearmors;

import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class BaseChestplate extends ArmorItem implements ToggleableTool, LeftClickableTool {
	protected final EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	protected final Map<Ability, AbilityParams> abilityParams = new EnumMap<>(Ability.class);

	public BaseChestplate(ArmorMaterial pMaterial, Properties pProperties) {
		super(pMaterial, EquipmentSlot.CHEST, pProperties);
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
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return Helpers.swapWithEquipmentSlot(this, level, player, hand);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected) {
		// itemSlot repeats across the hotbar and armor
		if (entity instanceof Player player && player.getItemBySlot(EquipmentSlot.CHEST) == itemStack && (!getPassiveTickAbilities(itemStack).isEmpty() || !getCooldownAbilities().isEmpty())) {
			armorTick(level, player, itemStack);
		}
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if (stack.getItem() instanceof PoweredTool) {
			amount = LivingEntityEvents.armorDamage(stack, amount, entity);
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
}
