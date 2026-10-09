package com.direwolf20.justdirethings.common.items.interfaces;

import com.direwolf20.justdirethings.client.particles.itemparticle.ItemFlowParticleData;
import com.direwolf20.justdirethings.common.events.BlockEvents;
import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.datagen.JustDireItemTags;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getInstantRFCost;

public class Helpers {
	public static final Predicate<BlockState> oreCondition = s -> s.is(Tags.Blocks.ORES) || s.is(JustDireBlockTags.CLUSTERS);
	public static final Predicate<BlockState> fallingBlockCondition = s -> s.getBlock() instanceof FallingBlock;
	public static final Predicate<BlockState> logCondition = s -> s.is(BlockTags.LOGS);

	public static void breakBlocksNew(Level level, BlockPos pos, LivingEntity pPlayer, ItemStack pStack, boolean damageTool, boolean instaBreak) {
		if (pPlayer instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
			BlockState state = level.getBlockState(pos);
			GameType type = player.getAbilities().instabuild ? GameType.CREATIVE : GameType.SURVIVAL;
			ItemStack mainHand = player.getMainHandItem();
			BlockEvent.BreakEvent exp = new BlockEvent.BreakEvent(level, pos, state, player);
			exp.setCanceled((!mainHand.isEmpty() && !mainHand.getItem().canAttackBlock(state, level, pos, player))
					|| player.blockActionRestricted(level, pos, type)
					|| (state.getBlock() instanceof GameMasterBlock && !player.canUseGameMasterBlocks()));
			exp.setExpToDrop(state.getExpDrop(level, pos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, pStack), EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, pStack)));
			if (MinecraftForge.EVENT_BUS.post(exp)) {
				player.connection.send(new ClientboundBlockUpdatePacket(pos, state));
				return;
			}
			BlockEntity blockEntity = level.getBlockEntity(pos);
			Block block = state.getBlock();
			//This is how vanilla does it? With assistance from Shadows!
			if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
				level.sendBlockUpdated(pos, state, state, 3);
				return;
			}
			if (player.blockActionRestricted(level, pos, type)) {
				return;
			}
			float destroySpeed = state.getDestroySpeed(level, pos);
			//removeBlock calls playerWillDestroy
			if (player.getAbilities().instabuild) {
				removeBlock(serverLevel, player, pos, state, false);
				return;
			}
			boolean canHarvest = state.canHarvestBlock(level, pos, player);
			//No mineBlock. The tool is damaged below.
			boolean removed = removeBlock(serverLevel, player, pos, state, canHarvest);
			if (canHarvest && removed) {
				BlockEvents.harvestPos = pos;
				BlockEvents.harvestExperience = exp.getExpToDrop();
				try {
					block.playerDestroy(level, player, pos, state, blockEntity, pStack);
				} finally {
					BlockEvents.harvestPos = null;
				}
				//Routed drops carry their experience with them
				if (exp.getExpToDrop() > 0 && !BlockEvents.routesDrops(player, pStack))
					block.popExperience(serverLevel, pos, exp.getExpToDrop());
			}
			if (damageTool && destroySpeed != 0.0F) {
				damageTool(pStack, pPlayer);
				if (instaBreak) {
					damageTool(pStack, pPlayer, getInstantRFCost(destroySpeed, level, pStack));
				}
			}
		}
	}

	public static boolean removeBlock(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, boolean canHarvest) {
		boolean removed = state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
		if (removed) {
			state.getBlock().destroy(level, pos, state);
		}
		return removed;
	}

	public static void damageTool(ItemStack stack, LivingEntity player) {
		if (player instanceof ServerPlayer serverPlayer && (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
			return;
		if (stack.getItem() instanceof PoweredTool poweredTool) {
			stack.hurtAndBreak(poweredTool.getBlockBreakFECost(), player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		} else {
			ItemStack cloneStack = stack.copy();
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			if (stack.isEmpty() && !cloneStack.isEmpty() && player instanceof Player player1)
				ForgeEventFactory.onPlayerDestroyItem(player1, cloneStack, InteractionHand.MAIN_HAND);
		}
	}

	public static void damageTool(ItemStack stack, LivingEntity player, int amount) {
		if (player instanceof ServerPlayer serverPlayer && (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
			return;
		if (stack.getItem() instanceof PoweredItem) {
			stack.hurtAndBreak(amount, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		} else {
			ItemStack cloneStack = stack.copy();
			stack.hurtAndBreak(amount, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			if (stack.isEmpty() && !cloneStack.isEmpty() && player instanceof Player player1)
				ForgeEventFactory.onPlayerDestroyItem(player1, cloneStack, InteractionHand.MAIN_HAND);
		}
	}

	public static void damageTool(ItemStack stack, LivingEntity player, Ability ability) {
		if (player instanceof ServerPlayer serverPlayer && (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
			return;
		if (stack.getItem() instanceof PoweredItem) {
			stack.hurtAndBreak(ability.getFeCost(), player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		} else {
			ItemStack cloneStack = stack.copy();
			stack.hurtAndBreak(ability.getDurabilityCost(), player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			if (stack.isEmpty() && !cloneStack.isEmpty() && player instanceof Player player1)
				ForgeEventFactory.onPlayerDestroyItem(player1, cloneStack, InteractionHand.MAIN_HAND);
		}
	}

	public static void damageTool(ItemStack stack, LivingEntity player, Ability ability, int multiplier) {
		if (player instanceof ServerPlayer serverPlayer && (serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR))
			return;
		if (stack.getItem() instanceof PoweredItem) {
			stack.hurtAndBreak(ability.getFeCost() * multiplier, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		} else {
			ItemStack cloneStack = stack.copy();
			stack.hurtAndBreak(ability.getDurabilityCost() * multiplier, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			if (stack.isEmpty() && !cloneStack.isEmpty() && player instanceof Player player1)
				ForgeEventFactory.onPlayerDestroyItem(player1, cloneStack, InteractionHand.MAIN_HAND);
		}
	}

	// hurtAndBreak skips creative players, but powered items still pay energy
	public static void damageItemInCreative(ItemStack stack, int amount, LivingEntity entity) {
		if (!entity.level.isClientSide && entity instanceof Player player && player.getAbilities().instabuild && stack.isDamageableItem())
			stack.getItem().damageItem(stack, amount, player, p -> {});
	}

	public static boolean playerHasShieldUseIntent(UseOnContext context) {
		Player player = context.getPlayer();
		return context.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().is(Items.SHIELD) && !player.isSecondaryUseActive();
	}

	// CONSUME means the block changed
	// useOn posts no game event, and creative players skip the energy cost
	public static InteractionResult finishToolAction(UseOnContext context, InteractionResult result) {
		if (result == InteractionResult.CONSUME) {
			Player player = context.getPlayer();
			context.getLevel().gameEvent(player, GameEvent.BLOCK_CHANGE, context.getClickedPos());
			if (player != null)
				damageItemInCreative(context.getItemInHand(), 1, player);
		}
		return result;
	}

	// ArmorItem.use only equips into an empty slot, so swap here
	public static InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item item, Level level, Player player, InteractionHand hand) {
		ItemStack heldStack = player.getItemInHand(hand);
		EquipmentSlot equipmentSlot = LivingEntity.getEquipmentSlotForItem(heldStack);
		ItemStack wornStack = player.getItemBySlot(equipmentSlot);
		if ((!EnchantmentHelper.hasBindingCurse(wornStack) || player.isCreative()) && !ItemStack.matches(heldStack, wornStack)) {
			if (!level.isClientSide())
				player.awardStat(Stats.ITEM_USED.get(item));

			ItemStack returnedStack = heldStack;
			if (!wornStack.isEmpty()) {
				returnedStack = wornStack.copy();
				wornStack.setCount(0);
			}
			ItemStack equippedStack = heldStack.copy();
			if (!player.isCreative())
				heldStack.setCount(0);
			player.setItemSlot(equipmentSlot, equippedStack);
			if (player.isCreative() && returnedStack != heldStack) {
				// useItem would give it the held piece's count and damage
				// Returning the held stack skips useItem's sync, so sync here
				player.setItemInHand(hand, returnedStack);
				if (!level.isClientSide())
					player.inventoryMenu.sendAllDataToRemote();
				return InteractionResultHolder.sidedSuccess(heldStack, level.isClientSide());
			}
			return InteractionResultHolder.sidedSuccess(returnedStack, level.isClientSide());
		}
		return InteractionResultHolder.fail(heldStack);
	}

	public static int testUseTool(ItemStack stack) {
		if (stack.getItem() instanceof PoweredTool poweredTool) {
			IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return -1; //Shouldn't Happen!
			return energyStorage.getEnergyStored() - poweredTool.getBlockBreakFECost();
		} else {
			if (!stack.isDamageableItem()) return 1;
			return stack.getMaxDamage() - stack.getDamageValue() - 1;
		}
	}

	public static int testUseTool(ItemStack stack, int cost) {
		if (stack.getItem() instanceof PoweredItem) {
			IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return -1; //Shouldn't Happen!
			return energyStorage.getEnergyStored() - cost;
		} else {
			return stack.getMaxDamage() - stack.getDamageValue() - cost;
		}
	}

	public static int testUseTool(ItemStack stack, Ability ability) {
		if (stack.getItem() instanceof PoweredItem) {
			IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return -1; //Shouldn't Happen!
			return energyStorage.getEnergyStored() - ability.getFeCost();
		} else {
			return stack.getMaxDamage() - stack.getDamageValue() - ability.getDurabilityCost();
		}
	}

	public static int testUseTool(ItemStack stack, Ability ability, int multiplier) {
		if (stack.getItem() instanceof PoweredItem) {
			IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
			if (energyStorage == null) return -1; //Shouldn't Happen!
			return energyStorage.getEnergyStored() - (ability.getFeCost() * multiplier);
		} else {
			return stack.getMaxDamage() - stack.getDamageValue() - (ability.getDurabilityCost() * multiplier);
		}
	}

	public static ItemStack getSmeltedItem(Level level, ItemStack itemStack) {
		RecipeManager recipeManager = level.getRecipeManager();
		ItemStack returnStack = ItemStack.EMPTY;
		Optional<SmeltingRecipe> smeltingRecipe = recipeManager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(itemStack), level);
		if (smeltingRecipe.isPresent() && !itemStack.is(JustDireItemTags.AUTO_SMELT_DENY))
			returnStack = smeltingRecipe.get().getResultItem();
		if (returnStack.isEmpty()) return itemStack;
		return returnStack;
	}

	public static List<ItemStack> smeltDrops(ServerLevel level, List<ItemStack> drops, ItemStack tool, LivingEntity entityLiving, boolean[] didISmelt) {
		List<ItemStack> returnList = new ArrayList<>();
		RecipeManager recipeManager = level.getRecipeManager();
		didISmelt[0] = false;
		for (ItemStack drop : drops) {
			// Check if there's a smelting recipe for the drop
			Optional<SmeltingRecipe> smeltingRecipe = recipeManager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(drop), level);

			if (smeltingRecipe.isPresent() && !drop.is(JustDireItemTags.AUTO_SMELT_DENY)) {
				// Get the result of the smelting recipe
				ItemStack smeltedResult = smeltingRecipe.get().getResultItem();

				if (!smeltedResult.isEmpty() && (testUseTool(tool, Ability.SMELTER, drop.getCount()) >= 0)) {
					// If the smelting result is valid, prepare to replace the original drop with the smelted result
					ItemStack resultStack = smeltedResult.copy();
					resultStack.setCount(drop.getCount()); // Assume all items in the stack are smelted
					if (!tool.isEmpty())
						damageTool(tool, entityLiving, Ability.SMELTER, drop.getCount());
					returnList.add(resultStack);
					didISmelt[0] = true;
				} else {
					returnList.add(drop);
				}
			} else {
				returnList.add(drop);
			}
		}
		return returnList;
	}

	public static void smokeDrop(ServerLevel level, ItemEntity drop, ItemStack tool, LivingEntity entityLiving, boolean[] didISmoke) {
		RecipeManager recipeManager = level.getRecipeManager();
		didISmoke[0] = false;

		// Check if there's a smoking recipe for the drop
		Optional<SmokingRecipe> smokingRecipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(drop.getItem()), level);

		if (smokingRecipe.isPresent() && !drop.getItem().is(JustDireItemTags.AUTO_SMOKE_DENY)) {
			// Get the result of the smoking recipe
			ItemStack smokedResults = smokingRecipe.get().getResultItem();

			if (!smokedResults.isEmpty() && (testUseTool(tool, Ability.SMOKER, drop.getItem().getCount()) >= 0)) {
				didISmoke[0] = true;
				smokedResults.setCount(drop.getItem().getCount()); // Assume all items in the stack are smoked
				// If the smoking result is valid, replace the original drop with the smoked result
				drop.setItem(smokedResults.copy());
				if (!tool.isEmpty())
					damageTool(tool, entityLiving, Ability.SMOKER, drop.getItem().getCount());
			}
		}
	}

	public static void dropDrops(List<ItemStack> drops, ServerLevel level, BlockPos dropAtPos) {
		for (ItemStack drop : drops) {
			ItemEntity itemEntity = new ItemEntity(level, dropAtPos.getX() + 0.5f, dropAtPos.getY() + 0.5f, dropAtPos.getZ() + 0.5f, drop);
			level.addFreshEntity(itemEntity);
		}
	}

	public static ItemStack teleportDrop(ItemStack itemStack, IItemHandler handler) {
		ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, itemStack, false);
		return leftover;
	}

	public static ItemStack teleportDrop(ItemStack itemStack, IItemHandler handler, ItemStack tool, Player player) {
		if (testUseTool(tool, Ability.DROPTELEPORT) < 0)
			return itemStack;
		ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, itemStack, false);
		if (leftover.isEmpty())
			damageTool(tool, player, Ability.DROPTELEPORT);
		return leftover;
	}

	public static void teleportDrops(List<ItemStack> drops, IItemHandler handler) {
		List<ItemStack> leftovers = new ArrayList<>();
		for (ItemStack drop : drops) {
			ItemStack leftover = teleportDrop(drop, handler);
			if (!leftover.isEmpty()) {
				leftovers.add(leftover);
			}
		}
		// Clear the original drops list and add all leftovers to it
		drops.clear();
		drops.addAll(leftovers);
	}

	public static void teleportDrops(List<ItemStack> drops, IItemHandler handler, ItemStack tool, Player player) {
		List<ItemStack> leftovers = new ArrayList<>();
		for (ItemStack drop : drops) {
			ItemStack leftover = teleportDrop(drop, handler, tool, player);
			if (!leftover.isEmpty()) {
				leftovers.add(leftover);
			}
		}
		// Clear the original drops list and add all leftovers to it
		drops.clear();
		drops.addAll(leftovers);
	}

	public static Set<BlockPos> findLikeBlocks(Level pLevel, BlockState pState, BlockPos pPos, int maxBreak, int range) {
		return findBlocks(pLevel, pState, pPos, maxBreak, range);
	}

	/**
	 * Basically a veinminer
	 */
	private static Set<BlockPos> findBlocks(Level pLevel, BlockState pState, BlockPos pPos, int maxBreak, int radius) {
		Set<BlockPos> foundBlocks = new HashSet<>(); //The matching Blocks
		Queue<BlockPos> blocksToCheck = new LinkedList<>(); //A list of blocks to check around
		Set<BlockPos> checkedBlocks = new HashSet<>(); //A list of blocks we already checked

		foundBlocks.add(pPos); //Obviously the block we broke is included in the return!
		blocksToCheck.add(pPos); //Start scanning around the block we broke

		while (!blocksToCheck.isEmpty()) {
			BlockPos posToCheck = blocksToCheck.poll(); //Get the next blockPos to scan around

			if (!checkedBlocks.add(posToCheck))
				continue; //Don't check blockPos we've checked before

			Set<BlockPos> matchingBlocks = BlockPos.betweenClosedStream(posToCheck.offset(-radius, -radius, -radius), posToCheck.offset(radius, radius, radius))
					.filter(blockPos -> pLevel.getBlockState(blockPos).is(pState.getBlock()))
					.map(BlockPos::immutable)
					.collect(Collectors.toSet());

			for (BlockPos toAdd : matchingBlocks) { //Ensure we don't go beyond maxBreak
				if (foundBlocks.size() < maxBreak) {
					foundBlocks.add(toAdd); //Add all the blocks we found to our set of found blocks
					if (!checkedBlocks.contains(toAdd))
						blocksToCheck.add(toAdd); //Add all the blocks we found to be checked as well
				} else
					return foundBlocks;
			}
		}
		return foundBlocks;
	}

	/**
	 * Looks in a specified direction for similar blocks - used by shovels to clear all like blocks above them
	 */
	public static Set<BlockPos> findBlocksSkyfall(Level pLevel, BlockPos pPos, int maxBreak, Direction direction, int range) {
		Set<BlockPos> foundBlocks = new HashSet<>(); //The matching Blocks
		foundBlocks.add(pPos); //Obviously the block we broke is included in the return!

		for (int i = 1; i < range; i++) {
			BlockPos posToCheck = pPos.relative(direction, i); //The next blockPos to check
			BlockState blockState = pLevel.getBlockState(posToCheck);
			if (fallingBlockCondition.test(blockState)) {
				foundBlocks.add(posToCheck);
			} else {
				break;
			}
			if (foundBlocks.size() >= maxBreak)
				break;
		}
		return foundBlocks;
	}

	/**
	 * Basically a veinminer
	 */
	public static Set<BlockPos> findTaggedBlocks(Level pLevel, List<TagKey<Block>> tags, BlockPos pPos, int maxBreak, int radius) {
		Set<BlockPos> foundBlocks = new HashSet<>(); //The matching Blocks
		Queue<BlockPos> blocksToCheck = new LinkedList<>(); //A list of blocks to check around
		Set<BlockPos> checkedBlocks = new HashSet<>(); //A list of blocks we already checked

		blocksToCheck.add(pPos); //Start scanning around the block we broke

		while (!blocksToCheck.isEmpty()) {
			BlockPos posToCheck = blocksToCheck.poll(); //Get the next blockPos to scan around

			if (!checkedBlocks.add(posToCheck))
				continue; //Don't check blockPos we've checked before

			Set<BlockPos> matchingBlocks = BlockPos.betweenClosedStream(posToCheck.offset(-radius, -radius, -radius), posToCheck.offset(radius, radius, radius))
					.filter(blockPos -> tags.stream().anyMatch(pLevel.getBlockState(blockPos)::is))
					.map(BlockPos::immutable)
					.collect(Collectors.toSet());

			for (BlockPos toAdd : matchingBlocks) { //Ensure we don't go beyond maxBreak
				if (foundBlocks.size() < maxBreak) {
					foundBlocks.add(toAdd); //Add all the blocks we found to our set of found blocks
					if (!checkedBlocks.contains(toAdd))
						blocksToCheck.add(toAdd); //Add all the blocks we found to be checked as well
				} else
					return foundBlocks;
			}
		}
		return foundBlocks;
	}

	public static boolean doLavaRepair(ItemStack stack, ItemEntity entity) {
		if (isInLava(entity)) {
			stack.getOrCreateTag().putInt("floatingTicks", 0);
			stack.addTagElement("lavaBlockLoc", NbtUtils.writeBlockPos(entity.blockPosition()));
			entity.setPickUpDelay(85);
			entity.setOnGround(true);
		}
		if (!validateLava(stack, entity)) {
			stack.removeTagKey("floatingTicks");
			stack.removeTagKey("lavaBlockLoc");
			entity.setOnGround(false);
		}
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("floatingTicks")) {
			if (entity.position().y - getLavaPos(stack).getY() < 3)
				entity.setDeltaMovement(new Vec3(0, 0.05, 0)); // Slower floating effect
			else
				entity.setDeltaMovement(new Vec3(0, 0.005, 0)); // Slower floating effect
			entity.move(MoverType.SELF, entity.getDeltaMovement());
			tag.putInt("floatingTicks", tag.getInt("floatingTicks") + 1);
			entity.setOnGround(true);
			if (tag.getInt("floatingTicks") >= 80) {
				turnLavaIntoObsidian(stack, entity);
				repairItem(stack);
				stack.removeTagKey("floatingTicks");
				stack.removeTagKey("lavaBlockLoc");
				entity.setOnGround(false);
			}
			if (!entity.level.isClientSide && tag.getInt("floatingTicks") < 40)
				doParticles(stack, entity);
		}
		return false; // Return false to allow normal item update processing
	}

	private static BlockPos getLavaPos(ItemStack stack) {
		CompoundTag lavaPos = stack.getTagElement("lavaBlockLoc");
		return lavaPos == null ? BlockPos.ZERO : NbtUtils.readBlockPos(lavaPos);
	}

	private static void doParticles(ItemStack itemStack, ItemEntity entity) {
		Random random = new Random();
		BlockPos pos = getLavaPos(itemStack);
		ItemFlowParticleData data = new ItemFlowParticleData(new ItemStack(Registration.GooBlock_Tier2.get()), entity.getX(), entity.getY() + 0.75, entity.getZ(), 3);
		for (int i = 0; i < 5; i++) {
			double d0 = (double) pos.getX() + random.nextDouble();
			double d1 = (double) pos.getY() + 0.95;
			double d2 = (double) pos.getZ() + random.nextDouble();
			((ServerLevel) entity.level).sendParticles(data, d0, d1, d2, 1, 0, 0, 0, 0);
		}
	}

	private static boolean isInLava(ItemEntity entity) {
		BlockState blockState = entity.level.getBlockState(entity.blockPosition());
		return blockState.is(Blocks.LAVA) && blockState.getValue(BlockStateProperties.LEVEL) == 0;
	}

	private static void repairItem(ItemStack stack) {
		stack.setDamageValue(0);
	}

	private static boolean validateLava(ItemStack stack, ItemEntity entity) {
		BlockState blockState = entity.level.getBlockState(getLavaPos(stack));
		return blockState.is(Blocks.LAVA) && blockState.getValue(BlockStateProperties.LEVEL) == 0;
	}

	private static void turnLavaIntoObsidian(ItemStack stack, ItemEntity entity) {
		BlockPos lavaPos = getLavaPos(stack);
		entity.level.setBlock(lavaPos, Blocks.OBSIDIAN.defaultBlockState(), 3); // Remove the lava block
		entity.level.playSound(null, lavaPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1F, 1.0F);
	}
}
