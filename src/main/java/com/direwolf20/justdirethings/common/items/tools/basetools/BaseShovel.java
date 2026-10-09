package com.direwolf20.justdirethings.common.items.tools.basetools;

import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class BaseShovel extends ShovelItem implements ToggleableTool, LeftClickableTool {
	protected final EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	protected final Map<Ability, AbilityParams> abilityParams = new EnumMap<>(Ability.class);

	public BaseShovel(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties) {
		super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		if (bindDrops(pContext))
			return InteractionResult.SUCCESS;
		useOnAbility(pContext);
		return Helpers.finishToolAction(pContext, super.useOn(pContext));
	}

	@Override
	public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
		return true; //We handle damage in the BlockEvent.BreakEvent
	}

	@Override
	public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
		if (!hurtEnemyAbility(pStack, pTarget, pAttacker))
			return false;
		Helpers.damageItemInCreative(pStack, 2, pAttacker); // Same as DiggerItem.hurtEnemy
		return super.hurtEnemy(pStack, pTarget, pAttacker);
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
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide && player.isShiftKeyDown())
			openSettings(player);
		useAbility(level, player, hand);
		return super.use(level, player, hand);
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
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		if (oldStack.is(newStack.getItem())) return false;
		return super.shouldCauseBlockBreakReset(oldStack, newStack);
	}
}
