package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.FluidPlacerT2BE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.FluidPlacerT2Container;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidPlacerT2 extends BaseMachineBlock {
	public FluidPlacerT2() {
		super(Properties.of(Material.METAL, MaterialColor.NONE)
				.sound(SoundType.METAL)
				.strength(2.0f)
				.isRedstoneConductor(BaseMachineBlock::never)
		);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FluidPlacerT2BE(pos, state);
	}

	@Override
	public void openMenu(Player player, BlockPos blockPos) {
		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new FluidPlacerT2Container(windowId, playerInventory, blockPos), new TranslatableComponent("")), (buf -> {
			buf.writeBlockPos(blockPos);
		}));
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && useFluidContainer(player.getItemInHand(hand), level, blockPos, player, hand, hit.getDirection(), false))
			return InteractionResult.SUCCESS;
		return super.use(blockState, level, blockPos, player, hand, hit);
	}

	@Override
	public boolean isValidBE(BlockEntity blockEntity) {
		return blockEntity instanceof FluidPlacerT2BE;
	}
}
