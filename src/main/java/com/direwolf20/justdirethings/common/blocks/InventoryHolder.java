package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.InventoryHolderContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class InventoryHolder extends BaseMachineBlock {
	public InventoryHolder() {
		super(Properties.of(Material.METAL, MaterialColor.NONE)
				.sound(SoundType.METAL)
				.strength(2.0f)
				.isRedstoneConductor(BaseMachineBlock::never)
		);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new InventoryHolderBE(pos, state);
	}

	@Override
	public void openMenu(Player player, BlockPos blockPos) {
		NetworkHooks.openGui((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new InventoryHolderContainer(windowId, playerInventory, blockPos), new TranslatableComponent("")), (buf -> {
			buf.writeBlockPos(blockPos);
		}));
	}

	@Override
	public boolean isValidBE(BlockEntity blockEntity) {
		return blockEntity instanceof InventoryHolderBE;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) { //Replica of Default Block onRemove - to skip dropping items.
			level.removeBlockEntity(pos);
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(world, pos, state, entity, stack);
		if (!world.isClientSide && entity instanceof Player) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof InventoryHolderBE inventoryHolderBE) {
				CompoundTag compound = stack.getTagElement(DROP_DATA);
				if (compound != null && !compound.isEmpty()) {
					inventoryHolderBE.loadInventory(compound);
				}
			}
		}
	}

	@Override
	protected void saveDropData(BaseMachineBE baseMachineBE, CompoundTag compoundTag) {
		super.saveDropData(baseMachineBE, compoundTag);
		((InventoryHolderBE) baseMachineBE).saveInventory(compoundTag);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return null;
	}

	//Override BaseMachineBlock being rotatable
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
