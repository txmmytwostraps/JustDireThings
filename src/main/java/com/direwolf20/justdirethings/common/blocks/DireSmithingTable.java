package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.DireSmithingTableBE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.DireSmithingTableContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DireSmithingTable extends BaseMachineBlock {
	private static final Component CONTAINER_TITLE = new TranslatableComponent("container.upgrade");

	public DireSmithingTable() {
		super(Properties.of(Material.METAL, MaterialColor.NONE)
				.sound(SoundType.METAL)
				.strength(2.0f)
				.isRedstoneConductor(BaseMachineBlock::never)
		);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DireSmithingTableBE(pos, state);
	}

	@Override
	public void openMenu(Player player, BlockPos blockPos) {
		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new DireSmithingTableContainer(windowId, playerInventory, blockPos), CONTAINER_TITLE), (buf -> {
			buf.writeBlockPos(blockPos);
		}));
	}

	@Override
	public boolean isValidBE(BlockEntity blockEntity) {
		return blockEntity instanceof DireSmithingTableBE;
	}

	//Not rotatable, like the vanilla smithing table
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state;
	}

	@Override
	public BlockState direRotate(BlockState blockState, LevelAccessor level, BlockPos pos, Rotation direction) {
		return blockState;
	}
}
