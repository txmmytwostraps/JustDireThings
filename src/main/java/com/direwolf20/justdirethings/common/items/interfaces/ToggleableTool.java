package com.direwolf20.justdirethings.common.items.interfaces;

import com.direwolf20.justdirethings.common.blockentities.GooSoilBE;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilBase;
import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.events.BlockEvents;
import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseBoots;
import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseChestplate;
import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseHelmet;
import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseLeggings;
import com.direwolf20.justdirethings.common.items.tools.utils.GooTier;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MiningCollect;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.direwolf20.justdirethings.common.items.interfaces.Helpers.*;

public interface ToggleableTool extends ToggleableItem {
	EnumSet<Ability> getAllAbilities();
	EnumSet<Ability> getAbilities();

	Map<Ability, AbilityParams> getAbilityParamsMap();

	default AbilityParams getAbilityParams(Ability toolAbility) {
		return getAbilityParamsMap().getOrDefault(toolAbility, new AbilityParams(-1, -1, -1));
	}

	default void registerAbility(Ability ability) {
		getAllAbilities().add(ability);
	}

	default void registerAbility(Ability ability, AbilityParams abilityParams) {
		getAllAbilities().add(ability);
		getAbilityParamsMap().put(ability, abilityParams);
	}

	default boolean hasAbility(Ability ability) {
		return getAbilities().contains(ability);
	}

	default List<Ability> getUseOnAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if (ability.useType == Ability.UseType.USE_ON && canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getActiveAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.USE || ability.useType == Ability.UseType.USE_COOLDOWN) && canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getPassiveTickAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.PASSIVE_TICK || ability.useType == Ability.UseType.PASSIVE_TICK_COOLDOWN) && canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getCooldownAbilities() {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if (ability.useType == Ability.UseType.USE_COOLDOWN || ability.useType == Ability.UseType.PASSIVE_COOLDOWN || ability.useType == Ability.UseType.PASSIVE_TICK_COOLDOWN)
				abilityList.add(ability);
		}
		return abilityList;
	}

	/**
	 * Used for toggling on and off via hotkey
	 */
	default List<Ability> getAllPassiveAbilities() {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.PASSIVE || ability.useType == Ability.UseType.PASSIVE_TICK || ability.useType == Ability.UseType.PASSIVE_COOLDOWN || ability.useType == Ability.UseType.PASSIVE_TICK_COOLDOWN))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default boolean canUseAbility(ItemStack itemStack, Ability toolAbility) {
		return hasAbility(toolAbility) && hasUpgrade(itemStack, toolAbility) && getEnabled(itemStack) && getSetting(itemStack, toolAbility.getName());
	}

	default boolean canUseAbilityAndDurability(ItemStack itemStack, Ability toolAbility) {
		return canUseAbility(itemStack, toolAbility) && (testUseTool(itemStack, toolAbility) >= 0);
	}

	default void openSettings(Player player) {
		player.openMenu(new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new ToolSettingContainer(windowId, playerInventory, player), new TranslatableComponent("")));
	}

	//Abilities
	default boolean hurtEnemyAbility(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
		damageTool(pStack, pAttacker);
		return true;
	}

	static int getMaxBreak(ItemStack pStack) {
		int maxBreak = Config.TOOL_MAX_BREAK_FERRICORE.get();
		if (pStack.getItem() instanceof TieredItem tieredItem) {
			if (tieredItem.getTier().equals(GooTier.BLAZEGOLD))
				maxBreak = Config.TOOL_MAX_BREAK_BLAZEGOLD.get();
			else if (tieredItem.getTier().equals(GooTier.CELESTIGEM))
				maxBreak = Config.TOOL_MAX_BREAK_CELESTIGEM.get();
			else if (tieredItem.getTier().equals(GooTier.ECLIPSEALLOY))
				maxBreak = Config.TOOL_MAX_BREAK_ECLIPSEALLOY.get();
		}
		return maxBreak;
	}

	default Set<BlockPos> getBreakBlockPositions(ItemStack pStack, Level pLevel, BlockPos pPos, LivingEntity pEntityLiving, BlockState pState) {
		Set<BlockPos> breakBlockPositions = new HashSet<>();
		int maxBreak = getMaxBreak(pStack);
		if (canUseAbility(pStack, Ability.OREMINER) && oreCondition.test(pState) && pStack.isCorrectToolForDrops(pState)) {
			breakBlockPositions.addAll(findLikeBlocks(pLevel, pState, pPos, maxBreak, 2));
		}
		if (canUseAbility(pStack, Ability.TREEFELLER) && logCondition.test(pState) && pStack.isCorrectToolForDrops(pState)) {
			breakBlockPositions.addAll(findLikeBlocks(pLevel, pState, pPos, maxBreak, 2));
		}
		if (canUseAbility(pStack, Ability.HAMMER)) {
			breakBlockPositions.addAll(MiningCollect.collect(pEntityLiving, pPos, getTargetLookDirection(pEntityLiving), pLevel, getToolValue(pStack, Ability.HAMMER.getName()), MiningCollect.SizeMode.AUTO, pStack));
		}
		breakBlockPositions.add(pPos);
		if (canUseAbility(pStack, Ability.SKYSWEEPER) && pStack.isCorrectToolForDrops(pState)) {
			Set<BlockPos> newPos = new HashSet<>();
			for (BlockPos blockPos : breakBlockPositions) {
				BlockState blockState = pLevel.getBlockState(blockPos.above());
				if (fallingBlockCondition.test(blockState))
					newPos.addAll(findBlocksSkyfall(pLevel, blockPos.above(), maxBreak, Direction.UP, maxBreak));
			}
			breakBlockPositions.addAll(newPos);
		}
		return breakBlockPositions;
	}

	default boolean canInstaBreak(ItemStack pStack, Level pLevel, Set<BlockPos> breakBlockPositions) {
		boolean instaBreak = false;
		if (canUseAbility(pStack, Ability.INSTABREAK)) { //Only Instabreak if we can instabreak ALL blocks in the area!
			float cumulativeDestroy = 0;
			for (BlockPos pos : breakBlockPositions) {
				BlockState blockState = pLevel.getBlockState(pos);
				float destroySpeedTarget = blockState.getDestroySpeed(pLevel, pos);
				cumulativeDestroy = cumulativeDestroy + destroySpeedTarget;
			}
			int rfCostInstaBreak = getInstantRFCost(cumulativeDestroy, pLevel, pStack);
			if (testUseTool(pStack, rfCostInstaBreak) > 0)
				instaBreak = true;
		}
		return instaBreak;
	}

	default void mineBlocksAbility(ItemStack pStack, Level pLevel, BlockPos pPos, LivingEntity pEntityLiving) {
		BlockState pState = pLevel.getBlockState(pPos);
		Set<BlockPos> breakBlockPositions = getBreakBlockPositions(pStack, pLevel, pPos, pEntityLiving, pState);
		boolean instaBreak = canInstaBreak(pStack, pLevel, breakBlockPositions);
		BlockEvents.spawnDropsAtPos = pPos;
		for (BlockPos breakPos : breakBlockPositions) {
			if (testUseTool(pStack) < 0)
				break;
			breakBlocksNew(pLevel, breakPos, pEntityLiving, pStack, true, instaBreak);
		}
		BlockEvents.spawnDropsAtPos = BlockPos.ZERO;
	}

	static int getInstantRFCost(float cumulativeDestroy, Level level, ItemStack stack) {
		int rfCost = Math.max(Ability.INSTABREAK.getFeCost(), Ability.INSTABREAK.getFeCost() * (int) cumulativeDestroy);
		int efficiency = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
		if (efficiency > 0) {
			float discount = 0.1f * efficiency;
			rfCost = rfCost - (int) (rfCost * discount);
		}
		return rfCost;
	}

	static void smelterParticles(ServerLevel level, Set<BlockPos> oreBlocksList) {
		Random random = new Random();
		int iterations = oreBlocksList.size() > 10 ? 1 : 5;
		for (int i = 0; i < iterations; i++) {
			for (BlockPos pos : oreBlocksList) {
				double d0 = (double) pos.getX() + random.nextDouble();
				double d1 = (double) pos.getY() + random.nextDouble();
				double d2 = (double) pos.getZ() + random.nextDouble();
				level.sendParticles(ParticleTypes.FLAME, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
			}
		}
	}

	static void smokerParticles(ServerLevel level, BlockPos itemPos, int stackSize) {
		Random random = new Random();
		int iterations = stackSize < 10 ? 1 : 5;
		for (int i = 0; i < iterations; i++) {
			double d0 = (double) itemPos.getX() + random.nextDouble();
			double d1 = (double) itemPos.getY() + random.nextDouble();
			double d2 = (double) itemPos.getZ() + random.nextDouble();
			level.sendParticles(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, Vec3 pos) {
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			double d0 = pos.x() + random.nextDouble();
			double d1 = pos.y() - 0.5d + random.nextDouble();
			double d2 = pos.z() + random.nextDouble();
			level.sendParticles(ParticleTypes.PORTAL, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, BlockPos pos, int iterations) {
		Random random = new Random();
		for (int i = 0; i < iterations; i++) {
			double d0 = (double) pos.getX() + random.nextDouble();
			double d1 = (double) pos.getY() - 0.5d + random.nextDouble();
			double d2 = (double) pos.getZ() + random.nextDouble();
			level.sendParticles(ParticleTypes.PORTAL, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, Set<BlockPos> oreBlocksList) {
		int iterations = oreBlocksList.size() > 10 ? 1 : 5;
		for (BlockPos pos : oreBlocksList) {
			teleportParticles(level, pos, iterations);
		}
	}

	static List<ToolRecords.AbilityCooldown> getAbilityCooldowns(ItemStack itemStack) {
		List<ToolRecords.AbilityCooldown> abilityCooldowns = new ArrayList<>();
		CompoundTag tag = itemStack.getTag();
		if (tag == null) return abilityCooldowns;
		ListTag listTag = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		for (int i = 0; i < listTag.size(); i++) {
			abilityCooldowns.add(ToolRecords.AbilityCooldown.fromTag(listTag.getCompound(i)));
		}
		return abilityCooldowns;
	}

	static void setAbilityCooldowns(ItemStack itemStack, List<ToolRecords.AbilityCooldown> abilityCooldowns) {
		ListTag listTag = new ListTag();
		for (ToolRecords.AbilityCooldown abilityCooldown : abilityCooldowns) {
			listTag.add(abilityCooldown.toTag());
		}
		itemStack.getOrCreateTag().put("cooldowns", listTag);
	}

	static int getAnyCooldown(ItemStack itemStack, Ability ability) {
		for (ToolRecords.AbilityCooldown abilityCooldown : getAbilityCooldowns(itemStack)) {
			if (abilityCooldown.abilityName().equals(ability.getName()))
				return abilityCooldown.cooldownTicks();
		}
		return -1;
	}

	static int getCooldown(ItemStack itemStack, Ability ability, boolean active) {
		for (ToolRecords.AbilityCooldown abilityCooldown : getAbilityCooldowns(itemStack)) {
			if (abilityCooldown.abilityName().equals(ability.getName()) && abilityCooldown.isactive() == active) {
				return abilityCooldown.cooldownTicks();
			}
		}
		return -1;
	}

	static void tickCooldowns(Level level, ItemStack itemStack, Player player) {
		if (level.isClientSide) return;
		List<ToolRecords.AbilityCooldown> abilityCooldowns = getAbilityCooldowns(itemStack);
		if (abilityCooldowns.isEmpty()) return;
		Set<ToolRecords.AbilityCooldown> cooldownsToRemove = new HashSet<>();
		for (int i = 0; i < abilityCooldowns.size(); i++) {
			ToolRecords.AbilityCooldown abilityCooldown = abilityCooldowns.get(i);
			int cooldown = abilityCooldown.cooldownTicks();
			boolean active = abilityCooldown.isactive();
			cooldown = cooldown - 1;
			if (cooldown == 0) {
				if (!active)
					cooldownsToRemove.add(abilityCooldown);
				else {
					Ability ability = Ability.valueOf(abilityCooldown.abilityName().toUpperCase(Locale.ROOT));
					if (itemStack.getItem() instanceof ToggleableTool toggleableTool) {
						AbilityParams abilityParams = toggleableTool.getAbilityParams(ability);
						ToolRecords.AbilityCooldown updatedCooldown = new ToolRecords.AbilityCooldown(
								abilityCooldown.abilityName(), abilityParams.cooldown, false
						);
						abilityCooldowns.set(i, updatedCooldown);
						player.playNotifySound(SoundEvents.CONDUIT_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
						cooldownDataClear(itemStack, ability);
					}
				}
			} else {
				ToolRecords.AbilityCooldown updatedCooldown = new ToolRecords.AbilityCooldown(
						abilityCooldown.abilityName(), cooldown, abilityCooldown.isactive()
				);
				abilityCooldowns.set(i, updatedCooldown);
			}
		}
		for (ToolRecords.AbilityCooldown cooldown : cooldownsToRemove) {
			abilityCooldowns.remove(cooldown);
			player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		if (abilityCooldowns.isEmpty())
			itemStack.removeTagKey("cooldowns");
		else
			setAbilityCooldowns(itemStack, abilityCooldowns);
	}

	static void cooldownDataClear(ItemStack itemStack, Ability ability) {
		if (ability == Ability.STUPEFY)
			AbilityMethods.clearStupefyTargets(itemStack);
	}

	static void addCooldown(ItemStack itemStack, Ability ability, int cooldown, boolean active) {
		List<ToolRecords.AbilityCooldown> abilityCooldowns = getAbilityCooldowns(itemStack);
		ToolRecords.AbilityCooldown cooldownRecord = new ToolRecords.AbilityCooldown(
				ability.getName(), cooldown, active
		);
		abilityCooldowns.add(cooldownRecord);
		setAbilityCooldowns(itemStack, abilityCooldowns);
	}

	static boolean isItemEquipped(ItemStack itemStack, Player player) {
		if (itemStack.getItem() instanceof BaseBoots)
			return ItemStack.isSameItemSameTags(itemStack, player.getItemBySlot(EquipmentSlot.FEET));
		if (itemStack.getItem() instanceof BaseLeggings)
			return ItemStack.isSameItemSameTags(itemStack, player.getItemBySlot(EquipmentSlot.LEGS));
		if (itemStack.getItem() instanceof BaseChestplate)
			return ItemStack.isSameItemSameTags(itemStack, player.getItemBySlot(EquipmentSlot.CHEST));
		if (itemStack.getItem() instanceof BaseHelmet)
			return ItemStack.isSameItemSameTags(itemStack, player.getItemBySlot(EquipmentSlot.HEAD));
		return ItemStack.isSameItemSameTags(itemStack, player.getItemInHand(InteractionHand.MAIN_HAND)) || ItemStack.isSameItemSameTags(itemStack, player.getItemInHand(InteractionHand.OFF_HAND));
	}

	default boolean useAbility(Level level, Player player, ItemStack itemStack, int keyCode, boolean isMouse) {
		boolean anyRan = false;
		Set<Ability> customBindAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			customBindAbilities.addAll(LeftClickableTool.getCustomBindingListFor(itemStack, keyCode, isMouse, player));
		for (Ability ability : getActiveAbilities(itemStack)) {
			if (customBindAbilities.contains(ability)) {
				if (ability.action != null) {
					ability.action.execute(level, player, itemStack);
				}
			}
		}
		if (!level.isClientSide) {
			for (Ability ability : getAllPassiveAbilities()) {
				if (customBindAbilities.contains(ability)) {
					if (ability.settingType == Ability.SettingType.CYCLE) {
						String abilityName = ability.getName();
						ToggleableTool.cycleSetting(itemStack, abilityName);
						boolean is_enabled = ToggleableTool.getSetting(itemStack, abilityName);
						if (is_enabled) {
							int currentValue = getToolValue(itemStack, abilityName);
							player.displayClientMessage(new TranslatableComponent("justdirethings.ability", new TranslatableComponent(ability.getLocalization() + "_" + currentValue), new TranslatableComponent("justdirethings.enabled")), true);
						} else {
							player.displayClientMessage(new TranslatableComponent("justdirethings.ability", new TranslatableComponent(ability.getLocalization()), new TranslatableComponent("justdirethings.disabled")), true);
						}
					} else {
						ToggleableTool.toggleSetting(itemStack, ability.getName());
						player.displayClientMessage(new TranslatableComponent("justdirethings.ability", new TranslatableComponent(ability.getLocalization()), ToggleableTool.getSetting(itemStack, ability.getName()) ? new TranslatableComponent("justdirethings.enabled") : new TranslatableComponent("justdirethings.disabled")), true);
					}
				}
			}
		}
		return anyRan;
	}

	default boolean useAbility(Level level, Player player, InteractionHand hand, boolean rightClick) {
		if (player.isShiftKeyDown()) return false;
		ItemStack itemStack = player.getItemInHand(hand);
		boolean anyRan = false;
		Set<Ability> leftClickAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			leftClickAbilities.addAll(LeftClickableTool.getLeftClickList(itemStack));
		for (Ability ability : getActiveAbilities(itemStack)) {
			if ((rightClick && LeftClickableTool.getBindingMode(itemStack, ability) == 0) || (!rightClick && leftClickAbilities.contains(ability))) {
				if (ability.action != null) {
					ability.action.execute(level, player, itemStack);
				}
			}
		}
		return anyRan;
	}

	default boolean armorTick(Level level, Player player, ItemStack itemStack) {
		boolean anyRan = false;
		for (Ability ability : getPassiveTickAbilities(itemStack)) {
			if (ability.action.execute(level, player, itemStack))
				anyRan = true;
		}
		tickCooldowns(level, itemStack, player);
		return anyRan;
	}

	default boolean useOnAbility(UseOnContext pContext, ItemStack itemStack, int keyCode, boolean isMouse) {
		if (pContext.getPlayer().isShiftKeyDown()) return false;
		boolean anyRan = false;
		Set<Ability> customBindAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			customBindAbilities.addAll(LeftClickableTool.getCustomBindingListFor(itemStack, keyCode, isMouse, pContext.getPlayer()));
		for (Ability ability : getUseOnAbilities(itemStack)) {
			if (customBindAbilities.contains(ability)) {
				if (ability.useOnAction != null) {
					if (ability.useOnAction.execute(pContext))
						anyRan = true;
				}
			}
		}
		return anyRan;
	}

	default boolean useOnAbility(UseOnContext pContext, boolean rightClick) {
		if (pContext.getPlayer().isShiftKeyDown()) return false;
		ItemStack itemStack = pContext.getItemInHand();
		boolean anyRan = false;
		Set<Ability> leftClickAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			leftClickAbilities.addAll(LeftClickableTool.getLeftClickList(itemStack));
		for (Ability ability : getUseOnAbilities(itemStack)) {
			if ((rightClick && !leftClickAbilities.contains(ability)) || (!rightClick && leftClickAbilities.contains(ability))) {
				if (ability.useOnAction != null) {
					if (ability.useOnAction.execute(pContext))
						anyRan = true;
				}
			}
		}
		return anyRan;
	}

	default boolean useAbility(Level level, Player player, InteractionHand hand) {
		return useAbility(level, player, hand, true);
	}

	default boolean useOnAbility(UseOnContext pContext) {
		return useOnAbility(pContext, true);
	}

	default boolean bindDrops(UseOnContext pContext) {
		Player player = pContext.getPlayer();
		if (player == null) return false;
		if (!player.isShiftKeyDown()) return false;
		ItemStack pStack = pContext.getItemInHand();
		if (!(pStack.getItem() instanceof ToggleableTool toggleableTool)) return false;
		if (!toggleableTool.canUseAbility(pStack, Ability.DROPTELEPORT)) return false;
		Level pLevel = pContext.getLevel();
		BlockPos pPos = pContext.getClickedPos();
		BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
		if (blockEntity == null) return false;
		IItemHandler handler = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, pContext.getClickedFace()).orElse(null);
		if (handler == null) return false;
		if (pContext.getLevel().isClientSide) return true;
		NBTHelpers.BoundInventory boundInventory = ToggleableTool.getBoundInventory(pStack);
		NBTHelpers.BoundInventory newBind = new NBTHelpers.BoundInventory(GlobalPos.of(pLevel.dimension(), pPos), pContext.getClickedFace());
		if (boundInventory != null && boundInventory.equals(newBind)) {
			removeBoundInventory(pStack);
			pContext.getPlayer().displayClientMessage(new TranslatableComponent("justdirethings.bindremoved"), true);
			player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
		} else {
			setBoundInventory(pStack, newBind);
			pContext.getPlayer().displayClientMessage(new TranslatableComponent("justdirethings.boundto", new TranslatableComponent(pLevel.dimension().location().getPath()), "[" + pPos.toShortString() + "]"), true);
			player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return true;
	}

	default boolean bindSoil(UseOnContext pContext) {
		boolean bindingSuccess = false;
		if (!pContext.getPlayer().level.isClientSide) {
			Level pLevel = pContext.getLevel();
			Player player = pContext.getPlayer();
			ItemStack heldItem = pContext.getItemInHand();
			BlockPos clickedPos = pContext.getClickedPos();
			BlockState blockState = pLevel.getBlockState(clickedPos);
			if (blockState.getBlock() instanceof GooSoilBase) {
				if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
					if (toggleableTool.canUseAbility(heldItem, Ability.DROPTELEPORT)) {
						if (Helpers.testUseTool(heldItem, Ability.DROPTELEPORT) > 0) {
							NBTHelpers.BoundInventory boundInventory = ToggleableTool.getBoundInventory(heldItem);
							if (boundInventory != null) {
								BlockEntity blockEntity = pLevel.getBlockEntity(clickedPos);
								if (blockEntity instanceof GooSoilBE gooSoilBE) {
									gooSoilBE.bindInventory(boundInventory);
									gooSoilBE.invalidateHandler();
									pContext.getPlayer().displayClientMessage(new TranslatableComponent("justdirethings.boundto", new TranslatableComponent(boundInventory.globalPos().dimension().location().getPath()), "[" + boundInventory.globalPos().pos().toShortString() + "]"), true);
									player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
									Helpers.damageTool(heldItem, player, Ability.DROPTELEPORT);
									bindingSuccess = true;
								}
							}
						}
						if (!bindingSuccess) {
							pContext.getPlayer().displayClientMessage(new TranslatableComponent("justdirethings.bindfailed").withStyle(ChatFormatting.RED), true);
							player.playNotifySound(SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
		return bindingSuccess;
	}

	//Thanks Soaryn!
	@NotNull
	static Direction getTargetLookDirection(LivingEntity livingEntity) {
		var playerLook = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getEyeHeight(), livingEntity.getZ());
		var lookVec = livingEntity.getViewVector(1.0F);
		var reach = livingEntity instanceof Player player ? player.getReachDistance() : 1;
		var endLook = playerLook.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
		var hitResult = livingEntity.level.clip(new ClipContext(playerLook, endLook, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));
		return hitResult.getDirection().getOpposite();
	}

	static void toggleSetting(ItemStack stack, String setting) {
		Ability toolAbility = Ability.byName(setting);
		stack.getOrCreateTag().putBoolean(toolAbility.getName(), !getSetting(stack, toolAbility.getName()));
	}

	/**
	 * Cycle will move through each possible value of a tool ability, incrementing by the increment value.
	 * When it reaches the end, it'll next disable the ability entirely. Next cycle it will re-enable it at the min value
	 */
	static void cycleSetting(ItemStack stack, String setting) {
		Ability toolAbility = Ability.valueOf(setting.toUpperCase(Locale.ROOT));
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		int currentValue = getToolValue(stack, setting);
		int nextValue = Math.min(abilityParams.maxSlider, currentValue + abilityParams.increment);
		if (nextValue == currentValue && getSetting(stack, toolAbility.getName())) { //If the next value is equal to the current one, its because we max'd out, so toggle it off
			setSetting(stack, setting, false);
			nextValue = abilityParams.minSlider;
		} else if (currentValue == abilityParams.minSlider && !getSetting(stack, toolAbility.getName())) {
			nextValue = abilityParams.minSlider;
			setSetting(stack, setting, true);
		}
		setToolValue(stack, setting, nextValue);
	}

	static void setBoundInventory(ItemStack stack, NBTHelpers.BoundInventory boundInventory) {
		stack.addTagElement("boundinventory", NBTHelpers.BoundInventory.toNBT(boundInventory));
	}

	static void removeBoundInventory(ItemStack stack) {
		stack.removeTagKey("boundinventory");
	}

	@Nullable
	static NBTHelpers.BoundInventory getBoundInventory(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("boundinventory");
		return tag == null ? null : NBTHelpers.BoundInventory.fromNBT(tag);
	}

	static IItemHandler getBoundHandler(ServerLevel serverLevel, ItemStack stack) {
		NBTHelpers.BoundInventory boundInventory = getBoundInventory(stack);
		if (boundInventory != null)
			return MiscHelpers.getAttachedInventory(serverLevel.getServer().getLevel(boundInventory.globalPos().dimension()), boundInventory.globalPos().pos(), boundInventory.direction());
		return null;
	}

	static void setSetting(ItemStack stack, String setting, boolean value) {
		Ability toolAbility = Ability.byName(setting);
		stack.getOrCreateTag().putBoolean(toolAbility.getName(), value);
	}

	//Also reads ToggleableItem's "enabled" flag
	static boolean getSetting(ItemStack stack, String setting) {
		CompoundTag tag = stack.getTag();
		return tag == null || !tag.contains(setting) || tag.getBoolean(setting); //Enabled by default
	}

	static boolean hasUpgrade(ItemStack stack, Ability ability) {
		if (!ability.requiresUpgrade()) return true;
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean("upgrade_" + ability.getName());
	}

	static void setCustomSetting(ItemStack stack, String setting, int value) {
		Ability toolAbility = Ability.byName(setting);
		stack.getOrCreateTag().putInt(toolAbility.getName() + "_custom", value);
	}

	static int getCustomSetting(ItemStack stack, String setting) {
		Ability toolAbility = Ability.byName(setting);
		CompoundTag tag = stack.getTag();
		return tag == null ? 0 : tag.getInt(toolAbility.getName() + "_custom");
	}

	static void setToolValue(ItemStack stack, String setting, int value) {
		Ability toolAbility = Ability.byName(setting);
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		int min = abilityParams.minSlider;
		int max = abilityParams.maxSlider;
		int setValue = Math.max(min, Math.min(max, value));
		stack.getOrCreateTag().putInt(toolAbility.getName() + "_value", setValue);
	}

	static int getToolValue(ItemStack stack, String setting) {
		Ability toolAbility = Ability.byName(setting);
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		int min = abilityParams.minSlider;
		int max = abilityParams.maxSlider;
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains(toolAbility.getName() + "_value"))
			return Math.max(min, Math.min(max, tag.getInt(toolAbility.getName() + "_value")));
		return abilityParams.defaultValue;
	}
}
