package com.direwolf20.justdirethings.common.items.tools.basetools;

import com.direwolf20.justdirethings.common.items.interfaces.Helpers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.Optional;

public class BasePaxel extends BasePickaxe {
	public BasePaxel(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties) {
		super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Player player = context.getPlayer();

		//Axe Stripping
		if (Helpers.playerHasShieldUseIntent(context)) {
			return InteractionResult.PASS;
		} else {
			Optional<BlockState> optional = this.evaluateNewBlockState(level, blockpos, player, level.getBlockState(blockpos), context);
			if (!optional.isEmpty()) {
				ItemStack itemstack = context.getItemInHand();
				if (player instanceof ServerPlayer) {
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
				}

				level.setBlock(blockpos, optional.get(), 11);
				level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockpos);
				if (player != null) {
					Helpers.damageItemInCreative(itemstack, 1, player);
					itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		//Shovel
		BlockState blockstate = level.getBlockState(blockpos);
		if (context.getClickedFace() != Direction.DOWN) {
			BlockState blockstate1 = blockstate.getToolModifiedState(context, ToolActions.SHOVEL_FLATTEN, false);
			BlockState blockstate2 = null;
			if (blockstate1 != null && level.getBlockState(blockpos.above()).isAir()) {
				level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
				blockstate2 = blockstate1;
			} else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
				// Forge has no tool action for this, so copy ShovelItem
				CampfireBlock.dowse(player, level, blockpos, blockstate);
				blockstate2 = blockstate.setValue(CampfireBlock.LIT, false);
				if (!level.isClientSide()) {
					level.levelEvent(null, 1009, blockpos, 0);
				}
			}

			if (blockstate2 != null) {
				if (!level.isClientSide) {
					level.setBlock(blockpos, blockstate2, 11);
					level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockpos);
					if (player != null) {
						Helpers.damageItemInCreative(context.getItemInHand(), 1, player);
						context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
					}
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		return super.useOn(context);
	}

	private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext p_40529_) {
		Optional<BlockState> optional = Optional.ofNullable(state.getToolModifiedState(p_40529_, ToolActions.AXE_STRIP, false));
		if (optional.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			return optional;
		} else {
			Optional<BlockState> optional1 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ToolActions.AXE_SCRAPE, false));
			if (optional1.isPresent()) {
				level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.levelEvent(player, 3005, pos, 0);
				return optional1;
			} else {
				Optional<BlockState> optional2 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ToolActions.AXE_WAX_OFF, false));
				if (optional2.isPresent()) {
					level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.levelEvent(player, 3004, pos, 0);
					return optional2;
				} else {
					return Optional.empty();
				}
			}
		}
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
				ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction) ||
				ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE));
	}

	@Override
	public float getDestroySpeed(ItemStack pStack, BlockState state) {
		return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE)) ? super.getDestroySpeed(pStack, Blocks.COBBLESTONE.defaultBlockState()) : 1.0F; //Possible hacky way to do this? :)
	}
}
