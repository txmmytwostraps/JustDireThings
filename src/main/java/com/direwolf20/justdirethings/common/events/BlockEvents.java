package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.interfaces.AbilityMethods;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.common.items.tools.BlazegoldHoe;
import com.direwolf20.justdirethings.common.items.tools.CelestigemHoe;
import com.direwolf20.justdirethings.common.items.tools.EclipseAlloyHoe;
import com.direwolf20.justdirethings.common.items.tools.FerricoreHoe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockEvents {
	public static boolean alreadyBreaking = false;
	public static BlockPos spawnDropsAtPos = BlockPos.ZERO;
	// Other mods may change the break event's XP, so keep it
	public static BlockPos harvestPos = null;
	public static int harvestExperience = 0;
	
	@SubscribeEvent
	public static void BlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) {
		ItemStack heldItem = event.getHeldItemStack();
		if (event.getToolAction().equals(ToolActions.HOE_TILL) && heldItem.getItem() instanceof ToggleableItem toggleableItem) {
			if (heldItem.getItem() instanceof FerricoreHoe && toggleableItem.getEnabled(heldItem)) {
				BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
				if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
					event.setFinalState(Registration.GooSoil_Tier1.get().defaultBlockState());
			} else if (heldItem.getItem() instanceof BlazegoldHoe && toggleableItem.getEnabled(heldItem)) {
				BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
				if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
					event.setFinalState(Registration.GooSoil_Tier2.get().defaultBlockState());
			} else if (heldItem.getItem() instanceof CelestigemHoe && toggleableItem.getEnabled(heldItem)) {
				BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
				if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
					event.setFinalState(Registration.GooSoil_Tier3.get().defaultBlockState());
			} else if (heldItem.getItem() instanceof EclipseAlloyHoe && toggleableItem.getEnabled(heldItem)) {
				BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
				if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
					event.setFinalState(Registration.GooSoil_Tier4.get().defaultBlockState());
			}
		}
	}

	@SubscribeEvent
	public static void BlockBreakEvent(BlockEvent.BreakEvent event) {
		ItemStack itemStack = event.getPlayer().getMainHandItem();
		if (!alreadyBreaking && itemStack.getItem() instanceof ToggleableTool toggleableTool && itemStack.isCorrectToolForDrops(event.getState())) {
			alreadyBreaking = true;
			toggleableTool.mineBlocksAbility(itemStack, event.getPlayer().level, event.getPos(), event.getPlayer());
			alreadyBreaking = false;
			spawnDropsAtPos = BlockPos.ZERO;
			event.setCanceled(true); //Cancel the original block broken, since its handled in the mineBlocksAbility
		}
	}

	/**
	 * Called from BlockMixin, since Forge has no event for block drops. Returns true when the tool took the drops.
	 */
	public static boolean BlockDrops(BlockState state, Level level, BlockPos breakPos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack itemStack) {
		if (routesDrops(breaker, itemStack) && breaker instanceof Player player && level instanceof ServerLevel serverLevel) {
			List<ItemStack> newDrops = new ArrayList<>();
			for (ItemStack drop : Block.getDrops(state, serverLevel, breakPos, blockEntity, breaker, itemStack)) {
				//Same checks as Block.popResource
				if (!drop.isEmpty() && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !serverLevel.restoringBlockSnapshots)
					newDrops.add(drop);
			}
			int droppedExperience = breakPos.equals(harvestPos) ? harvestExperience : state.getExpDrop(serverLevel, breakPos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, itemStack), EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack));
			BlockPos spawnAt = spawnDropsAtPos == null || spawnDropsAtPos.equals(BlockPos.ZERO) ? player.blockPosition() : spawnDropsAtPos;
			AbilityMethods.handleDrops(itemStack, serverLevel, spawnAt, player, breakPos, newDrops, state, droppedExperience);
			state.spawnAfterBreak(serverLevel, breakPos, itemStack);
			return true;
		}
		return false;
	}

	public static boolean routesDrops(@Nullable Entity breaker, ItemStack itemStack) {
		return alreadyBreaking && itemStack.getItem() instanceof ToggleableTool && breaker instanceof Player;
	}
}
